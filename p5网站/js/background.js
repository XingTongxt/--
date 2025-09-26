document.addEventListener('DOMContentLoaded', () => {
  const images = [
    '../img/p5back.png',
    '../img/p5back0.png',
    '../img/p5back1.png',
    '../img/p5back2.jpg'
  ];
  let index = 0;
  const body = document.body;

  const layer1 = document.createElement('div');
  const layer2 = document.createElement('div');
  layer1.className = 'bg-layer';
  layer2.className = 'bg-layer';
  document.body.appendChild(layer1);
  document.body.appendChild(layer2);

  
  setTimeout(() => {
    layer1.style.backgroundImage = `url(${images[index]})`;
    layer1.classList.add('active');
    index = (index + 1) % images.length;
  }, 1000);

  
  setInterval(() => {
    const nextLayer = layer1.classList.contains('active') ? layer2 : layer1;
    nextLayer.style.backgroundImage = `url(${images[index]})`;
    nextLayer.classList.add('active');

    const prevLayer = nextLayer === layer1 ? layer2 : layer1;
    prevLayer.classList.remove('active');

    index = (index + 1) % images.length;
  }, 5000);
});