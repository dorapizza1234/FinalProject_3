/**
 * 관리자 광고 관리 JS
 */
let currentAdId = null;

document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("adDetailModal");

    if (modal) {
        modal.addEventListener("show.bs.modal", function (event) {

            const button = event.relatedTarget;
            const adId = button.getAttribute("data-adid");
            currentAdId = adId;

            console.log("Fetching Detail for ID:", adId);

			fetch("ad/detail?adId=" + adId)
			.then(res => res.json())
			.then(data => {
				console.log("서버 응답:", data);   // ⭐ 추가

				    const ad = data.ad;
				    const conflict = data.conflict;

				    if(!ad){
				        console.log("ad 값 없음");
				        return;
				    }
			 

			    if(!ad){
			        alert("광고 데이터를 불러오지 못했습니다.");
			        return;
			    }

			    document.getElementById("brandName").innerText = ad.brandName || "-";
			    document.getElementById("managerName").innerText = ad.managerName || "-";
			    document.getElementById("phone").innerText = ad.phone || "-";
			    document.getElementById("content").innerText = ad.content || "-";

			    document.getElementById("period").innerText =
			        (ad.startDate || "") + " ~ " + (ad.endDate || "");

			    const adImg = document.getElementById("adImage");
			    const downBtn = document.getElementById("downloadBtn");

			    if(ad.filePath){
			        const path = "/upload/" + ad.filePath;
			        adImg.src = path;
			        downBtn.href = path;
			    } else {
			        adImg.style.display = "none";
			        downBtn.style.display = "none";
			    }

			    const conflictMsg = document.getElementById("conflictMessage");
			    const approveBtn = document.getElementById("approveBtn");

			    if(conflict){
			        conflictMsg.innerText = "⚠ 이미 해당 기간에 광고가 있습니다.";
			        approveBtn.disabled = true;
			    }else{
			        conflictMsg.innerText = "";
			        approveBtn.disabled = false;
			    }

			})
			.catch(err => {
			    console.error("광고 상세 불러오기 실패:", err);
			});
        });
    }
});

/**
 * 광고 승인
 */
function approveAd() {

    if (!currentAdId) return;

    if (confirm("이 광고를 승인하시겠습니까?")) {

       fetch("ad/approve?adId=" + currentAdId, {
            method: "POST"
        })
        .then(res => {

            if (res.ok) {
                alert("승인되었습니다.");
                location.reload();
            } else {
                alert("승인 처리 실패 (상태: " + res.status + ")");
            }

        })
        .catch(err => {
            console.error("승인 요청 중 에러:", err);
        });
    }
}


/**
 * 광고 반려
 */
function rejectAd() {

    if (!currentAdId) return;

    const reason = prompt("반려 사유를 입력해주세요.");

    if (!reason) return;

   fetch("ad/reject?adId=" + currentAdId + "&reason=" + encodeURIComponent(reason), {
        method: "POST"
    })
    .then(res => {

        if (res.ok) {
            alert("반려 처리가 완료되었습니다.");
            location.reload();
        } else {
            alert("반려 처리 실패 (상태: " + res.status + ")");
        }

    })
    .catch(err => {
        console.error("반려 요청 중 에러:", err);
    });
}

console.log("admin_ad.js loaded");