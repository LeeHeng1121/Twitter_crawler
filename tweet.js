var twitter = require('twitter');
var key = require('./key.js');
var geo = require('./geo.js');
var symbols = require('./symbols.js')
var db = require('./model/db.js');
var tweetModel = db.Tweet;
var client = new twitter(key.keyinfo);
var check_dt =0;
var now_dt = new Date();
var debugdt = new Date();
var n=0;


function startStream(callback) {
    client.stream('statuses/sample', {}, function(stream) {
      console.log("start reading tweet");

      stream.on('data', function(tweet) {
        callback(tweet);
      });

      stream.on('error', function(error) {
          throw error;
      });
    });
}


startStream(function(tweet){
  now_dt=new Date();
  if(now_dt.getMinutes() != check_dt){
    console.log("*****"+now_dt+"*****");
    check_dt=now_dt.getMinutes();
  }
	if(tweet.lang=='ja'){
        var newTweet = new tweetModel(tweet);
        newTweet.save();
	}

  //  symbols.CountSymbols(tweet);
});

return;
var kafka = require('./kafka-core/kafka.js')

kafka.startProducer(function(producer){
    console.log("Kafka Producer is ready...");
    startStream(function(tweet){
        var payloads = [
            { topic: 'twitter', messages: JSON.stringify(tweet) }
        ];
        producer.send(payloads, function (err, data) {
            if(err) throw err;
        });
    });
});
