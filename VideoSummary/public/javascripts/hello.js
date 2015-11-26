var gCurrentTime = 0; // Horrible global state for now, I'm so sorry
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

function setSummarizationStatus(str) {
    $("#summarize-status").text(str);
}

function highlightPlaylistIndex(index) {
    $($("#playlist-div").children()[index]).addClass("sel");}

function removeHighlightPlaylistIndex(index) {
    $($("#playlist-div").children()[gSliceIndex]).removeClass("sel");
}

function nextSlice() {
    if (gSliceIndex < gSlices.length - 1) {
        player.seekTo(gSlices[gSliceIndex + 1].startTimeSeconds);
    } else {
        stopSummarization();
        stopVideo();
        setSummarizationStatus("Stopped");
    }
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
        setSummarizationStatus("Stopped");
    }
}

function stopSummarization() {
    clearInterval(gTimerid);
    $($("#playlist-div").children()[gSliceIndex]).removeClass("sel");
    gSliceIndex = 0;
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
        stopSummarization();
        return;
    }

    if (player.getPlayerState() == YT.PlayerState.PLAYING && player.getCurrentTime() > timeSlices[0].endTimeSeconds) {
        timeSlices.splice(0, 1);

        if (timeSlices.length > 0) {
            player.pauseVideo();
            player.seekTo(timeSlices[0].startTimeSeconds);
            console.log("Seeking to " + timeSlices[0].startTimeSeconds);
            player.playVideo();
            removeHighlightPlaylistIndex(gSliceIndex);
            gSliceIndex++;
            highlightPlaylistIndex(gSliceIndex);
        } else {
            // Reset everything and stop the video
            stopSummarization();
            stopVideo();
            setSummarizationStatus("Stopped");
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    $("#summarizeButton").on('click', function() {

        var apiRequest = $.post("/times/" + window.vvv, function(data) {
            alert("success " + data);
            var slices = JSON.parse(data); // Expecting an array back! (It should probably get changed to an object though) TODO
            gSlices = JSON.parse(data);
            setSummarizationStatus("Successfully retrieved summary, playing...");
            //$("#summarize-status").text("Retrieved summary, playing...");
            if (slices.length > 0) {
                $("#playlist-div").empty(); // Clear the playlist
                for (var i = 0; i < slices.length; i++) {
                    $("#playlist-div").append('<div class="section">' + i + '.) ' +
                        slices[i].startTime + ' - ' + slices[i].endTime + '<br/>' +
                        slices[i].wordsSpoken.substring(0, 50) +
                        '...</div>');
                }

                highlightPlaylistIndex(gSliceIndex); // Highlight the current playlist index

                player.pauseVideo();
                player.seekTo(slices[0].startTimeSeconds);
                player.playVideo();
            }

            gTimerid = setInterval(checkCurrentTime, 500, slices);
        });

        apiRequest.fail(function(data) {
            setSummarizationStatus("Response from server was a failure " + data); // TODO create better error response
        });
    });

    $("#stopButton").on('click', function() {
        stopSummarization();
        setSummarizationStatus("Stopped");
        stopVideo();
    });

    $("#nextButton").on('click', function() {
       nextSlice();
    });
}, false);