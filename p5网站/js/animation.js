window.addEventListener("DOMContentLoaded", () => {
  const items = document.querySelectorAll(".menu-item");
  items.forEach((item, i) => {
    item.style.opacity = 0;
    item.style.transform += " translateX(-100px)";
    setTimeout(() => {
      item.style.transition = "all 0.5s ease";
      item.style.opacity = 1;
      item.style.transform = "skew(-10deg) translateX(0)";
    }, i * 200 + 500); // 每个按钮依次出现
  });
});
