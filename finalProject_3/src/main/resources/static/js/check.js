document.addEventListener("DOMContentLoaded", function () {

    const ctxPath = (window.ctxPath || "").replace(/\/$/, "");
    const searchBtn = document.getElementById("searchBtn");
    const searchWordInput = document.getElementById("searchWord");

    function goPriceSearch(sortType, priceMode) {
        const searchWord = (searchWordInput?.value || "").trim();

        if (searchWord === "") {
            alert("검색어를 입력하세요.");
            searchWordInput?.focus();
            return;
        }

        let url = ctxPath + "/product/price_check?searchWord=" + encodeURIComponent(searchWord);
        url += "&sortType=" + encodeURIComponent(sortType || window.priceSortType || "latest");
        url += "&priceMode=" + encodeURIComponent(priceMode || window.priceMode || "list");

        location.href = url;
    }

    searchBtn?.addEventListener("click", function () {
        goPriceSearch("latest", window.priceMode || "list");
    });

    searchWordInput?.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            goPriceSearch("latest", window.priceMode || "list");
        }
    });

   

    document.querySelectorAll(".sps-sortBtn").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const sortType = btn.dataset.sort || "latest";
            goPriceSearch(sortType, window.priceMode || "list");
        });
    });

    if (!window.hasResult) {
        return;
    }

    const canvas = document.getElementById("mkChart");
    const tip = document.getElementById("mkTip");
    const priceEl = document.getElementById("mkPrice");
    const tabs = Array.from(document.querySelectorAll(".mk-tab"));
    const emptyMsg = document.getElementById("mkEmptyMsg");

    if (!canvas || !tip || !priceEl || tabs.length === 0) {
        return;
    }

    const ctx = canvas.getContext("2d");

    const css = getComputedStyle(document.documentElement);
    const COLORS = {
        green: css.getPropertyValue("--green").trim() || "#16c36c",
        blue: css.getPropertyValue("--blue").trim() || "#2f7df6",
        grid: css.getPropertyValue("--line").trim() || "#e9eef5",
        muted: css.getPropertyValue("--muted").trim() || "#6b7684"
    };

    const serverChartData = Array.isArray(window.priceChartData) ? window.priceChartData : [];
    const statsData = window.priceStatsData || null;

    let mode = window.priceMode || "list";
    let hitPoints = [];

    function fmtMoney(v) {
        return Math.round(Number(v || 0)).toLocaleString("ko-KR");
    }

    function fmtTick(v) {
        const n = Number(v || 0);

        if (n === 0) return "0원";
        if (n < 10000) return fmtMoney(n) + "원";
        if (n % 10000 === 0) return (n / 10000) + "만원";
        return (n / 10000).toFixed(1) + "만원";
    }

    function fmtMMDD(ymd) {
        if (!ymd || ymd.length < 10) return "";
        return ymd.substring(5, 7) + "/" + ymd.substring(8, 10);
    }

    let DATA = serverChartData.map(function (item) {
        return {
            x: fmtMMDD(item.priceDate),
            fullDate: item.priceDate,
            list: Number(item.avgPrice || 0),
            sale: 0
        };
    });

    function syncTabUI() {
        tabs.forEach(function (btn) {
            const btnMode = btn.dataset.mode || "";
            const isActive = btnMode === mode;

            btn.classList.toggle("is-active", isActive);
            btn.setAttribute("aria-selected", isActive ? "true" : "false");
        });
    }

    function getYMax(maxValue) {
        if (maxValue <= 0) return 10000;

        let yMax = Math.ceil(maxValue * 1.2 / 1000) * 1000;
        if (yMax < 10000) yMax = 10000;
        return yMax;
    }

    function buildYTicks(yMin, yMax) {
        const range = yMax - yMin;
        const unit = Math.ceil(range / 5 / 1000) * 1000 || 1000;
        const ticks = [];

        for (let i = 0; i <= 5; i++) {
            ticks.push(yMin + (unit * i));
        }

        return ticks;
    }

    function resizeCanvas() {
        const wrap = canvas.parentElement;
        const rect = wrap.getBoundingClientRect();
        const dpr = Math.max(1, window.devicePixelRatio || 1);

        if (rect.width === 0 || rect.height === 0) return;

        canvas.width = Math.floor(rect.width * dpr);
        canvas.height = Math.floor(rect.height * dpr);

        canvas.style.width = rect.width + "px";
        canvas.style.height = rect.height + "px";

        ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
        draw();
    }

    function draw() {
        const W = canvas.clientWidth;
        const H = canvas.clientHeight;

        if (W === 0 || H === 0) return;

        ctx.clearRect(0, 0, W, H);
        hideTip();

        const maxListValue = Math.max(...DATA.map(function (d) { return d.list || 0; }), 0);
        const Y_MIN = 0;
        const Y_MAX = getYMax(maxListValue);
        const Y_TICKS = buildYTicks(Y_MIN, Y_MAX);

        if (mode === "sale") {
            emptyMsg.style.display = "flex";
            emptyMsg.textContent = "판매가 기능은 아직 준비 중입니다.";
        }
        else {
            emptyMsg.style.display = "none";
        }

        const pad = { l: 60, r: 16, t: 16, b: 34 };
        const innerW = W - pad.l - pad.r;
        const innerH = H - pad.t - pad.b;
        const xCount = DATA.length;
        const xStep = xCount > 1 ? innerW / (xCount - 1) : 0;

        const yToPx = function (y) {
            const ratio = (y - Y_MIN) / (Y_MAX - Y_MIN || 1);
            return pad.t + innerH - (ratio * innerH);
        };

        const xToPx = function (i) {
            return xCount === 1 ? (pad.l + innerW / 2) : (pad.l + i * xStep);
        };

        ctx.save();
        ctx.lineWidth = 1;
        ctx.font = '12px system-ui, -apple-system, "Segoe UI", Roboto, "Noto Sans KR", Arial';
        ctx.textAlign = "right";
        ctx.textBaseline = "middle";
        ctx.fillStyle = COLORS.muted;

        Y_TICKS.forEach(function (val) {
            const y = yToPx(val);

            ctx.strokeStyle = COLORS.grid;
            ctx.setLineDash([3, 6]);
            ctx.beginPath();
            ctx.moveTo(pad.l, y);
            ctx.lineTo(W - pad.r, y);
            ctx.stroke();

            ctx.setLineDash([]);
            ctx.fillText(fmtTick(val), pad.l - 10, y);
        });

        ctx.restore();

        const listPts = DATA.map(function (d, i) {
            return {
                x: xToPx(i),
                y: yToPx(d.list),
                v: d.list,
                label: d.x,
                fullDate: d.fullDate,
                key: "등록가"
            };
        });

        hitPoints = [];

        if (mode === "all" || mode === "list") {
            hitPoints = listPts.map(function (p) {
                return {
                    x: p.x,
                    y: p.y,
                    v: p.v,
                    label: p.label,
                    fullDate: p.fullDate,
                    key: p.key,
                    color: COLORS.green
                };
            });

            drawLine(listPts, COLORS.green, 3);
            drawPoints(listPts, COLORS.green);
        }

        ctx.save();
        ctx.fillStyle = COLORS.muted;
        ctx.font = '12px system-ui, -apple-system, "Segoe UI", Roboto, "Noto Sans KR", Arial';
        ctx.textBaseline = "top";

        ctx.textAlign = "left";
        ctx.fillText(DATA[0].x, pad.l, H - pad.b + 8);

        if (DATA.length > 1) {
            ctx.textAlign = "right";
            ctx.fillText(DATA[DATA.length - 1].x, W - pad.r, H - pad.b + 8);
        }

        ctx.restore();
    }

    function drawLine(points, color, width) {
        if (!points || points.length === 0) return;

        ctx.save();
        ctx.strokeStyle = color;
        ctx.lineWidth = width;
        ctx.lineJoin = "round";
        ctx.lineCap = "round";

        ctx.beginPath();
        ctx.moveTo(points[0].x, points[0].y);

        for (let i = 1; i < points.length; i++) {
            ctx.lineTo(points[i].x, points[i].y);
        }

        ctx.stroke();
        ctx.restore();
    }

    function drawPoints(points, color) {
        ctx.save();

        points.forEach(function (p) {
            ctx.fillStyle = "#fff";
            ctx.beginPath();
            ctx.arc(p.x, p.y, 4.5, 0, Math.PI * 2);
            ctx.fill();

            ctx.fillStyle = color;
            ctx.beginPath();
            ctx.arc(p.x, p.y, 3, 0, Math.PI * 2);
            ctx.fill();
        });

        ctx.restore();
    }

    function updatePrice() {
        if (mode === "sale") {
            priceEl.innerHTML = `0<small>원</small>`;
            priceEl.style.color = COLORS.blue;
            return;
        }

        let v = 0;

        if (statsData && statsData.avgPrice != null) {
            v = Number(statsData.avgPrice);
        }

        priceEl.innerHTML = `${fmtMoney(v)}<small>원</small>`;
        priceEl.style.color = COLORS.green;
    }

    function hideTip() {
        tip.style.opacity = "0";
    }

    function showTip(p) {
        tip.style.opacity = "1";
        tip.style.left = p.x + "px";
        tip.style.top = p.y + "px";
        tip.innerHTML = `
            <div class="mk-tip-date">${p.fullDate}</div>
            <div class="mk-tip-price">등록가: ${fmtMoney(p.v)}원</div>
        `;
    }

    function getMousePos(e) {
        const rect = canvas.getBoundingClientRect();
        return {
            x: e.clientX - rect.left,
            y: e.clientY - rect.top
        };
    }

    function findNearestPoint(mx, my) {
        let best = null;
        let bestD = Infinity;

        for (const p of hitPoints) {
            const dx = p.x - mx;
            const dy = p.y - my;
            const d = Math.sqrt(dx * dx + dy * dy);

            if (d < bestD) {
                bestD = d;
                best = p;
            }
        }

        return (best && bestD <= 18) ? best : null;
    }

    canvas.addEventListener("mousemove", function (e) {
        if (mode === "sale") {
            hideTip();
            return;
        }

        const pos = getMousePos(e);
        const p = findNearestPoint(pos.x, pos.y);

        if (p) showTip(p);
        else hideTip();
    });

    canvas.addEventListener("mouseleave", hideTip);

    tabs.forEach(function (btn) {
        btn.addEventListener("click", function () {
            mode = btn.dataset.mode || "list";
            window.priceMode = mode;

            syncTabUI();
            updatePrice();
            draw();
            hideTip();
        });
    });

    syncTabUI();
    updatePrice();
    window.addEventListener("resize", resizeCanvas);
    resizeCanvas();
});