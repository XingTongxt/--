document.addEventListener('DOMContentLoaded', () => {
  const top = document.querySelector('#back-to-top');
  top.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
});