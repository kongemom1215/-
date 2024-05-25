import {GLTFLoader} from 'GLTFLoader';
import * as THREE from 'three';

let scene = new THREE.Scene();
let renderer = new THREE.WebGLRenderer({
    canvas : document.querySelector('#canvas'),
    antialias : true
});

renderer.outputEncoding = THREE.sRGBEncoding;

let camera = new THREE.PerspectiveCamera(50,1);
camera.position.set(0,0,5);
scene.background = new THREE.Color("rgb(248, 249, 250)");

let ambientLight = new THREE.AmbientLight('white', 0.52);
scene.add(ambientLight);
let light = new THREE.DirectionalLight('white', 0.52);
scene.add(light);

let loader = new GLTFLoader();

let rotationSpeed = 0.01;

loader.load('/potato/scene.gltf', function(gltf){
    scene.add(gltf.scene);

    function animate(){
        requestAnimationFrame(animate);
        gltf.scene.rotation.y += rotationSpeed;
        renderer.render(scene, camera);

        if (rotationSpeed >= 0.3) {
            $('#resetBtnDiv').removeClass("d-none");
        }
    }

    animate();
});

document.querySelector('#canvas').addEventListener('click', function() {
    rotationSpeed += 0.01;
});

document.querySelector('#resetBtn').addEventListener('click', function() {
    rotationSpeed = 0.01;
    $('#resetBtnDiv').addClass("d-none");
});