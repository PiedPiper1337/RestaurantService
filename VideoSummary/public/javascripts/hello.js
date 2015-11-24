var gCurrentTime = 0; // Horrible global state for now
var gTimerid;
var gSlices;
var gSliceIndex = 0;

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

/**
 * Function that is executed when the state of the player changes
 * */
function onPlayerStateChange(event) {
    console.log("State changed!");
    console.log("To: " + event.data.toString());
    if (event.data == YT.PlayerState.ENDED) {
        clearInterval(gTimerid);
        $("#summarize-status").text("Stopped summarization playback!");
        setTimeout(function() {
            $("#summarize-status").text("");
        }, 1500);
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
    console.log(player.getCurrentTime() + "waiting for " + timeSlices[0].endTimeSeconds);

    if (timeSlices.length == 0) {
        clearInterval(gTimerid);
        return;
    }

    if (player.getPlayerState() == YT.PlayerState.PLAYING && player.getCurrentTime() > timeSlices[0].endTimeSeconds) {
        timeSlices.splice(0, 1);

        if (timeSlices.length > 0) {
            player.pauseVideo();
            player.seekTo(timeSlices[0].startTimeSeconds);
            console.log("Seeking to " + timeSlices[0].startTimeSeconds);
            player.playVideo();
            $($("#playlist-div").children()[gSliceIndex]).removeClass("sel");
            gSliceIndex++;
            $($("#playlist-div").children()[gSliceIndex]).addClass("sel"); // Highlight the current playlist index
        } else {
            // Reset everything and stop the video
            player.stopVideo()
            $($("#playlist-div").children()[gSliceIndex]).removeClass("sel");
            gSliceIndex = 0;
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    $("#summarizeButton").on('click', function() {

        var apiRequest = $.post("/times/" + window.vvv, function(data) {
            alert("success " + data);
            var slices = JSON.parse(data); // Expecting an array back! (It should probably get changed to a object though) TODO
            gSlices = JSON.parse(data);
            $("#summarize-status").text("Retrieved summary, playing...");
            if (slices.length > 0) {
                $("#playlist-div").empty();
                for (var i = 0; i < slices.length; i++) {
                    $("#playlist-div").append('<div class="section">' + JSON.stringify(slices[i]) + '</div>');
                }

                $($("#playlist-div").children()[gSliceIndex]).addClass("sel"); // Highlight the current playlist index

                player.pauseVideo();
                player.seekTo(slices[0].startTimeSeconds);
                console.log("Initially seeking to " + slices[0].startTimeSeconds);
                player.playVideo();
            }

            console.log("starting the timer with this array: " + typeof(slices));
            gTimerid = setInterval(checkCurrentTime, 500, slices);
        });

        apiRequest.fail(function(data) {
            $("#summarize-status").text("Response from server was a failure " + data);
        });
    });
    // Put anything you want to happen when the page is finished loading
}, false);