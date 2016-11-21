var readline = require('linebyline'),
rl = readline('/media/neillab/yb/mongodb/bin/0523output.json',{maxLineLength:20000}),
moment = require('moment');
var mapreduce = require('mapred')(1);

//noFilterTweets 140454 count
var tweetsCount = 21216459;
// tweetsCount = 1000;
var timeMap = {};
var targetname = new Array();
var targetcount = new Array();
var counttime =1;
var prekey;
var key;
var ptr;
var fullkey;
var fullprekey;
var target;
var i=0;
var i1=0;
var ten_min_checktime;
var date;
var check_to_print=1;
var meaningless_threshold;
var total_tweet_count=0;
var total_hashtag_count=0;
var period_hashtag_count=0;
var period_meaningless_count=0;
var last_print_time;
var flag=0;
rl.on('line', function(eachLine, lineCount, byteCount) {
  var json = JSON.parse(eachLine);
  date = parseStringToDate(json);
  if(date.format("MM/DD")=="04/22"){
    flag =1;
  }
  if(flag==1){
    json = JSON.parse(eachLine);
    hashtagcount(json,false,0);
    date = parseStringToDate(json).format("mm");
    if(date%60!=0){
      check_to_print=1;
    }
    if(date%60==0 && check_to_print==1){
      //每過幾分鐘印一次結果
       if(last_print_time!=parseStringToDate(json).format("HH:mm")) {
         //避免印過07:00:00後07:00:59又印一遍
         hashtagcount(json,false,1);
       }

      check_to_print=0;
    }
    if(lineCount==tweetsCount-1) {
      //結束程式
        hashtagcount(json,true,1);
    }
  }

});

function parseStringToDate(json) {
    date = moment(json.created_at.$date,"YYYY-MM-DD-HH-mm-ss-----");
    return date;
}


function hashtagcount(json,stop,print) {
    var date = parseStringToDate(json)
    total_tweet_count +=1;
    fullprekey = fullkey;
    prekey= key;
    key = date.format("ss");
    fullkey = date.format("MM/DD HH:mm:ss")
    if(counttime==1){
      ten_min_checktime = date.format("mm");
    }
    counttime+=1;



          if(json.entities!=undefined && json.entities.hashtags!=undefined){

          var tagArray = json.entities.hashtags;
          var tagArrLength = tagArray.length;
              for (var i=0;i<tagArrLength;i++){
                  var tag = tagArray[i];
                  if(tag.text!="undefined"){
                  target = tag.text;
                  total_hashtag_count+=1;
                  period_hashtag_count+=1;
                  }
              }
              if(targetname.length == 0){
                targetname[0]=target;
                targetcount[0]=1;
              }

              else if (targetname.length != 0){
                for(i=0;i<targetname.length;i++){
                    if(target==targetname[i]){
                      targetcount[i]+=1;
                      break;
                    }
                    else if(target!=targetname[i] && i==targetname.length-1){
                    targetname[i+1] = target;
                    targetcount[i+1]=1;
                    break;
                    }
                  }

              }
          }




        for(i1=0;i1<targetname.length-1;i1++){
          for(i=0;i<targetname.length-1;i++){
            var nametemp;
            var counttemp;
            if(targetcount[i]<targetcount[i+1]){
              counttemp=targetcount[i];
              nametemp=targetname[i];
              targetcount[i]=targetcount[i+1];
              targetname[i]=targetname[i+1];
              targetcount[i+1]=counttemp;
              targetname[i+1]=nametemp;
            }
          }
        }

         if(print==1){
          last_print_time = date.format("HH:mm");
	  for(ptr=0; ptr<=9; ptr++){
          console.log(date.format("MM/DD	HH:mm	")+targetname[ptr]+ "	" +
          targetcount[ptr]);

	  }
          meaningless_threshold = period_hashtag_count/1000;
/*
          console.log("門檻值為:"+meaningless_threshold);
          for(i1=0;i1<targetcount.length;i1++){
            if(targetcount[i1]<meaningless_threshold){
            period_meaningless_count+=1;
            }
          }
          console.log("共有:"+period_meaningless_count +"個無意義hashtag");
          console.log("目前統計「含hashtag / 全tweet數」比例：" +
          total_hashtag_count/total_tweet_count);
*/
          targetname = new Array();
          targetcount = new Array();
          period_hashtag_count =0;
          period_meaningless_count=0;
        }
        if(stop){
          process.exit();
        }
    }



