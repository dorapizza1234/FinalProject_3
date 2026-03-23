(function () {
  const wrap = document.querySelector('.free-deal-slider__wrap');
  const viewport = document.querySelector('.free-deal-slider__viewport');
  const track = document.querySelector('#freeDealTrack');
  const prevBtn = document.querySelector('.free-deal-slider__nav--prev');
  const nextBtn = document.querySelector('.free-deal-slider__nav--next');
  const dotsWrap = document.querySelector('#freeDealDots');

  if (!wrap || !viewport || !track || !prevBtn || !nextBtn || !dotsWrap) return;

  const slides = Array.from(track.querySelectorAll('.free-deal-slider__item'));
  if (slides.length === 0) return;

  let startIndex = 0;
  let autoTimer = null;

  let isDown = false;
  let downX = 0;
  let downTranslate = 0;
  let currentTranslate = 0;

  const DOT_BLOCK_SIZE = 3;

  function visibleCount() {
    const w = window.innerWidth;
    if (w <= 640) return 1;
    if (w <= 1024) return 2;
    return 3;
  }

  function step() {
    const firstSlide = track.querySelector('.free-deal-slider__item');
    if (!firstSlide) return 0;

    const slideW = firstSlide.getBoundingClientRect().width;
    const gap = parseFloat(getComputedStyle(track).gap || '0') || 0;
    return slideW + gap;
  }

  function maxStartIndex() {
    return Math.max(0, slides.length - visibleCount());
  }

  function totalBlocks() {
    return Math.max(1, Math.ceil(slides.length / DOT_BLOCK_SIZE));
  }

  function currentBlock() {
    const v = visibleCount();
    const lastVisibleIndex = Math.min(slides.length - 1, startIndex + v - 1);
    return Math.floor(lastVisibleIndex / DOT_BLOCK_SIZE);
  }

  function buildDots() {
    dotsWrap.innerHTML = '';

    const total = totalBlocks();

    for (let i = 0; i < total; i++) {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'free-deal-slider__dot';
      btn.setAttribute('aria-label', (i + 1) + '번째 블럭');

      btn.addEventListener('click', function () {
        stopAuto();
        startIndex = i * DOT_BLOCK_SIZE;
        update(true, false);
        startAuto();
      });

      dotsWrap.appendChild(btn);
    }
  }

  function setActiveDot() {
    const dots = Array.from(dotsWrap.querySelectorAll('.free-deal-slider__dot'));
    const cur = currentBlock();

    dots.forEach(function (dot, i) {
      dot.classList.toggle('is-active', i === cur);
    });
  }

  function applyTransform(px, animate) {
    track.style.transition = animate ? 'transform 420ms ease' : 'none';
    track.style.transform = 'translateX(' + px + 'px)';
  }

  function update(animate = true, clamp = true) {
    if (clamp) {
      startIndex = Math.min(Math.max(startIndex, 0), maxStartIndex());
    }
    else {
      startIndex = Math.min(Math.max(startIndex, 0), slides.length - 1);
    }

    currentTranslate = -step() * startIndex;
    applyTransform(currentTranslate, animate);

    prevBtn.disabled = (startIndex <= 0);
    nextBtn.disabled = (startIndex >= maxStartIndex());

    setActiveDot();
  }

  function goPrevBlock() {
    startIndex -= visibleCount();
    update(true, true);
  }

  function goNextBlock() {
    startIndex += visibleCount();
    update(true, true);
  }

  prevBtn.addEventListener('click', function () {
    stopAuto();
    goPrevBlock();
    startAuto();
  });

  nextBtn.addEventListener('click', function () {
    stopAuto();
    goNextBlock();
    startAuto();
  });

  function getClientX(e) {
    if (e.touches && e.touches[0]) return e.touches[0].clientX;
    if (e.changedTouches && e.changedTouches[0]) return e.changedTouches[0].clientX;
    return e.clientX;
  }

  function onDown(e) {
    isDown = true;
    downX = getClientX(e);
    downTranslate = currentTranslate;
    applyTransform(currentTranslate, false);
    stopAuto();
  }

  function onMove(e) {
    if (!isDown) return;

    const dx = getClientX(e) - downX;
    const raw = downTranslate + dx;

    const min = -step() * maxStartIndex();
    const max = 0;

    const softened =
      raw < min ? min - (min - raw) * 0.25 :
      raw > max ? max + (raw - max) * 0.25 :
      raw;

    applyTransform(softened, false);
  }

  function onUp(e) {
    if (!isDown) return;
    isDown = false;

    const endX = getClientX(e);
    const dx = endX - downX;
    const threshold = Math.max(60, viewport.getBoundingClientRect().width * 0.12);

    if (dx > threshold) {
      goPrevBlock();
    }
    else if (dx < -threshold) {
      goNextBlock();
    }
    else {
      update(true, true);
    }

    startAuto();
  }

  viewport.addEventListener('mousedown', onDown);
  window.addEventListener('mousemove', onMove);
  window.addEventListener('mouseup', onUp);

  viewport.addEventListener('touchstart', onDown, { passive: true });
  viewport.addEventListener('touchmove', onMove, { passive: true });
  viewport.addEventListener('touchend', onUp);

  function startAuto() {
    stopAuto();

    if (slides.length <= visibleCount()) return;

    autoTimer = setInterval(function () {
      const v = visibleCount();
      const max = maxStartIndex();

      if (startIndex >= max) {
        startIndex = 0;
      }
      else {
        startIndex += v;
      }

      update(true, true);
    }, 4500);
  }

  function stopAuto() {
    if (autoTimer) {
      clearInterval(autoTimer);
    }
    autoTimer = null;
  }

  wrap.addEventListener('mouseenter', stopAuto);
  wrap.addEventListener('mouseleave', startAuto);

  window.addEventListener('resize', function () {
    buildDots();
    update(false, true);
  });

  buildDots();
  update(false, true);
  startAuto();
})();