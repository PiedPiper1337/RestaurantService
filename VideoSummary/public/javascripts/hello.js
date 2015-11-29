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
    //console.log(player.getCurrentTime() + "waiting for " + timeSlices[0].endTimeSeconds);

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
        setSummarizationStatus("Summarizing...");
        var apiRequest = $.post("/times/" + window.vvv, function(resp) {
            var response =  JSON.parse(resp);
            var groups = response.Groups;
            var wordcloud = response.WordCloud;
            var histogram = response.Histogram;
            window.tobias = response;
            //console.log(resp);
            console.log(JSON.stringify(histogram));
            var slices = groups;
            gSlices = groups;
            setSummarizationStatus("Successfully retrieved summary, playing...");
            if (slices.length > 0) {
                $("#playlist-div").empty(); // Clear the playlist
                for (var i = 0; i < slices.length; i++) {
                    $("#playlist-div").append('<div class="section">' + (i+1) + '.) ' +
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

            var werds = [];

            for (var key in wordcloud) {
                if (wordcloud.hasOwnProperty(key)) {
                    werds.push({
                        text: key,
                        size: wordcloud[key]
                    });
                }
            }

            /*
            * Add the analytics section of the page
            * */
            $("#wordcloud-div").remove();
            $("#histogram-div").remove();
            $("body").append('<div id="wordcloud-div"></div>');
            $("body").append('<div id="histogram-div"></div>');

            /*
            * Add the word cloud
            * */
            var fill = d3.scale.category20();

            d3.layout.cloud().size([500, 500])
                .words(werds)
                .rotate(function() { return ~~(Math.random() * 2) * 90; })
                .font("Impact")
                .fontSize(function(d) { return d.size; })
                .on("end", draw)
                .start();

            function draw(words) {
                d3.select("#wordcloud-div").append("svg")
                    .attr("width", 500)
                    .attr("height", 500)
                    .append("g")
                    .attr("transform", "translate(250,250)")
                    .selectAll("text")
                    .data(words)
                    .enter().append("text")
                    .style("font-size", function(d) { return d.size + "px"; })
                    .style("font-family", "Impact")
                    .style("fill", function(d, i) { return fill(i); })
                    .attr("text-anchor", "middle")
                    .attr("transform", function(d) {
                        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
                    })
                    .text(function(d) { return d.text; });
            }

            var histogramX = [];
            var histogramY = [];

            for (var i = 0; i < histogram.length; i++) {
                histogramY.push(histogram[i].importance);
                histogramX.push(histogram[i].startTimeSeconds);
            }

            var x = [];
            for (var i = 0; i < 500; i ++) {
                x[i] = Math.random();
            }

            var data = [
                {
                    autobiny: false,
                    ybins: {
                        start: 1,
                        end: 70,
                        size: 1
                    },

                    x: histogramX,
                    y: histogramY,
                    type: 'histogram'
                }
            ];

            var layout = {
                bargap: .25,
                xaxis: {title: "Seconds"},
                yaxis: {title: "Importance"},
            };

            Plotly.newPlot('histogram-div', data, layout);

            /*
            * Add the histogram
            *
            // Generate a Bates distribution of 10 random variables.
            var values = d3.range(100).map(d3.random.bates(10));

            // A formatter for counts.
            var formatCount = d3.format(",.0f");

            var margin = {top: 20, right: 20, bottom: 35, left: 20},
                width = 500 - margin.left - margin.right,
                height = 500 - margin.top - margin.bottom;

            var x = d3.scale.linear()
                .domain([histogram[0].startTimeSeconds, histogram[histogram.length - 1].startTimeSeconds])
                .range([0, width]);

            // Generate a histogram using twenty uniformly-spaced bins.
            var data = d3.layout.histogram()
                .bins(x.ticks(100))
                (histogram);

            var y = d3.scale.linear()
                .domain([0, d3.max(data, function(d) { return d.y; })])
                .range([height, 0]);

            var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

            var svg = d3.select("#histogram-div").append("svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

            var bar = svg.selectAll(".bar")
                .data(data)
                .enter().append("g")
                .attr("class", "bar")
                .attr("transform", function(d) { return "translate(" + x(d.x) + "," + y(d.y) + ")"; });

            bar.append("rect")
                .attr("x", 1)
                .attr("width", x(data[0].dx) - 1)
                .attr("height", function(d) { return height - y(d.y); });

            bar.append("text")
                .attr("dy", ".75em")
                .attr("y", 6)
                .attr("x", x(data[0].dx) / 2)
                .attr("text-anchor", "middle")
                .text(function(d) { return formatCount(d.y); });

            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);
                */
        });

        apiRequest.fail(function(data) {
            setSummarizationStatus("Response from server was a failure " + JSON.stringify(data)); // TODO create better error response
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