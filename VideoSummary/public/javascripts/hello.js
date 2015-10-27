/**
 * Three test sections of the video to play
 * */
var testSlices = [
    {start: 4, end: 10},
    {start: 12, end: 19},
    {start: 35, end: 45}
];

/**
 * The player object that allows us to interact with the iframe
 * */
var player;
function onYouTubeIframeAPIReady() {
    player = new YT.Player('videoDiv', {
        height: '450',
        width: '800',
        videoId: window.vvv,
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });
}

/**
 * Function that is executed once the iframe is ready to play
 * */
function onPlayerReady(event) {
    //event.target.playVideo();
    playSlices(testSlices);
}

var done = false;
/**
 * Function that is executed when the state of the player changes
 * */
function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.PLAYING && !done) {
        //setTimeout(stopVideo, 6000);
        done = true;
    }
}

function stopVideo() {
    player.stopVideo();
}

/**
 * Execute this to start playing all the sections in the testSlices array
 * */
function playSlices(arr) {
    console.log("testSlices has " + testSlices.length);

    if (arr.length > 0) {
        player.pauseVideo();
        player.seekTo(arr[0].start);
        player.playVideo();
        setTimeout(function () {
            playSlices(arr);
        }, (arr[0].end - arr[0].start) * 1000);
        arr.splice(0, 1);
    } else {
        player.stopVideo();
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // Put anything you want to happen when the page is finished loading
}, false);