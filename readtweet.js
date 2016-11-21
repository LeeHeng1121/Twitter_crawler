var readline = require('linebyline'),
rl = readline('./backup0425.json',{maxLineLength:20000}),
moment = require('moment');
var mapreduce = require('mapred')(1);
//noFilterTweets 140454 count
var tweetsCount = 1439968;
// tweetsCount = 1000;

var word1 = "食";
// var word1_1="ほしい";
var word2 = "買";
// var word2_1 = "かいたい";
var word3 = "行";
// var word3_1 = "いきたい";
// var word4 = "買って";
// var word4_1 = "かって";
// var word5 = "買います";
// var word5_1 = "かいます";

var count=0;
var hoshii=0;
var kaitai =0;
var ikitai =0;

var hoshii_hashtag_name= new Array();
var hoshii_hashtag_count = new Array();
var ikitai_hashtag_name= new Array();
var ikitai_hashtag_count = new Array();
var kaitai_hashtag_name = new Array();
var kaitai_hashtag_count = new Array();

rl.on('line', function(eachLine, lineCount, byteCount) {
    var json = JSON.parse(eachLine);
    console.log(count+=1);
    tweetread(json,false);

    if(lineCount==2098112-1) {
      //結束程式

    sort_hashtag(kaitai_hashtag_name,kaitai_hashtag_count);
    sort_hashtag(hoshii_hashtag_name,hoshii_hashtag_count);
    sort_hashtag(ikitai_hashtag_name,ikitai_hashtag_count);
    var i;
    console.log("食 tag count:");
    for(i=0;i<hoshii_hashtag_name.length-1;i++){
      console.log(hoshii_hashtag_name[i] + "+" + hoshii_hashtag_count[i]);
    }
    console.log("\n\n買 tag count:");
    for(i=0;i<kaitai_hashtag_name.length-1;i++){
      console.log(kaitai_hashtag_name[i] + "+" + kaitai_hashtag_count[i]);
    }
    console.log("\n\n行 tag count:");
    for(i=0;i<ikitai_hashtag_name.length-1;i++){
      console.log(ikitai_hashtag_name[i] + "+" + ikitai_hashtag_count[i]);
    }
    tweetread(json,true);
    }
});



function tweetread(json,stop) {

          if(json.text.indexOf(word1)!=-1
          // json.text.indexOf(word1_1)!=-1
              || json.text.indexOf(word2)!=-1
              // ||json.text.indexOf(word2_1)!=-1
              || json.text.indexOf(word3)!=-1){
              // ||json.text.indexOf(word3_1)!=-1){
              // || json.text.indexOf(word4)!=-1||json.text.indexOf(word4_1)!=-1
              // || json.text.indexOf(word5)!=-1||json.text.indexOf(word5_1)!=-1)

              if(json.text.indexOf(word1)!=-1){
                  // json.text.indexOf(word1_1)!=-1)
              hoshii+=1;
              count_hashtag(json,hoshii_hashtag_name,hoshii_hashtag_count)
              }
              if(json.text.indexOf(word2)!=-1){
                  // json.text.indexOf(word2_1)!=-1)
                  // json.text.indexOf(word4)!=-1||
                  // json.text.indexOf(word4_1)!=-1||
                  // json.text.indexOf(word5)!=-1||
                  // json.text.indexOf(word5_1)!=-1)
              kaitai+=1;
              count_hashtag(json,kaitai_hashtag_name,kaitai_hashtag_count)
              }
              if(json.text.indexOf(word3)!=-1){
                  // json.text.indexOf(word3_1)!=-1)
              ikitai+=1;
              count_hashtag(json,ikitai_hashtag_name,ikitai_hashtag_count)
              }

            // console.log(json.text);
            console.log("食＝" + hoshii + "買=" + kaitai +
            "行=" + ikitai);
            console.log("--------------------------");
          }

        if(stop){
          process.exit();
        }
    }

function count_hashtag(json,targetname,targetcount){
  if(json.entities!=undefined && json.entities.hashtags!=undefined){
    var tagArray = json.entities.hashtags;
    var tagArrLength = tagArray.length;
    var target;
    for (var i=0;i<tagArrLength;i++){
      var tag = tagArray[i];

      if(tag.text!="undefined"){
          target = tag.text;
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
  }
}

function sort_hashtag(targetname,targetcount){
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
}


