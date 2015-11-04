/**
 * Three test sections of the video to play
 *
var testSlices = [
    {start: 4, end: 10},
    {start: 12, end: 19},
    {start: 35, end: 45}
];
 */

var gCurrentTime = 0; // Horrible global state for now
var gTimerid;

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
    //playSlices(testSlices);
}

var done = false;

/**
 * Function that is executed when the state of the player changes
 * */
function onPlayerStateChange(event) {
    console.log("State changed!");
    console.log("To: " + event.data.toString());
    if (event.data == YT.PlayerState.PLAYING && !done) {
        //setTimeout(stopVideo, 6000);
        done = true;
    }
}

function stopVideo() {
    player.stopVideo();
}

/**
 * Function to poll the video periodically, getting the updated progress of the video
 * Don't run this directly
 * */
function checkCurrentTime(timeSlices) {
    console.log(player.getCurrentTime());

    if (timeSlices.length == 0) {
        clearInterval(gTimerid);
        return;
    }

    if (player.getPlayerState() == YT.PlayerState.PLAYING && player.getCurrentTime() > timeSlices[0].end) {
        timeSlices.splice(0, 1);

        if (timeSlices.length > 0) {
            player.pauseVideo();
            player.seekTo(timeSlices[0].start);
            player.playVideo();
        } else {
            player.stopVideo()
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    $("#summarizeButton").on('click', function() {
        var testSlices = [
            {start: 4, end: 10},
            {start: 12, end: 19},
            {start: 35, end: 45}
        ];

        if (testSlices.length > 0) {
            player.pauseVideo();
            player.seekTo(testSlices[0].start);
            player.playVideo();
        }

        gTimerid = setInterval(checkCurrentTime, 500, testSlices);
    });
    // Put anything you want to happen when the page is finished loading
}, false);