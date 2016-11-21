var readline = require('linebyline'),
rl = readline('/media/neillab/yb/mongodb/bin/0613output.json',{maxLineLength:20000}),
moment = require('moment');
var mapreduce = require('mapred')(1);

//noFilterTweets 140454 count
var tweetsCount = 32644775;
// tweetsCount = 1000;
var timeMap = {};
var targetname = new Array();
var targetcount = new Array();
var last_print_time=0;
var windows_time = 1440; //window長度設為幾分鐘
var windows_TO_timestamp = windows_time*=60000;
var All_tag_array;
All_tag_array=[];
var All_timestamp_array;
All_timestamp_array=[];
var i=0;j=0;

var timelock=0;
var main_hashtag=[];
var main_hashtag_count=[];
var related_hashtag=[];//二維
var related_hashtag_count=[];//二維
related_hashtag_count[0]=[];
related_hashtag_count[0][1]=1;
related_hashtag_count[1]=[];
related_hashtag_count[1][0]=1; //要宣告新的二維陣列 要重新先宣告一維在push上去
var fs = require('fs');
//一秒 = 1000
//一分鐘 = 60000
rl.on('line', function(eachLine, lineCount, byteCount) {
  var json = JSON.parse(eachLine);

    date = parseStringToDate(json).format("MM/DD");
    if(date == "04/22"){
    timelock=1;
    }
    if(timelock==1){
	if(last_print_time+windows_TO_timestamp<parseInt(parseStringToDate(json).format("x")) ) {

  //if(last_print_time+60000<parseInt(parseStringToDate(json).format("x")) ) {
  //每過1分鐘印一次結果

        //避免印過07:00:00後07:00:59又印一遍
        hashtagcount(json,false,1);
        last_print_time=parseInt(parseStringToDate(json).format("x"));
        }
        else if(lineCount==tweetsCount-1) {
        //結束程式
        hashtagcount(json,true,1);
        }
        else {
        hashtagcount(json,false,0); //這一行代表不要每一個tweet就印一次
        }


    }

});

function parseStringToDate(json) {
    date = moment(json.created_at.$date,"YYYY-MM-DD-HH-mm-ss-----");
    return date;
}

function hashtagcount(json,stop,print) {
    var timestamp = parseStringToDate(json).format("x");

    if(json.entities!=undefined && json.entities.hashtags!=undefined){
            var tagArray = json.entities.hashtags;
            var tagArrLength = tagArray.length;

            if(tagArrLength>1){

              for(i=0;i<All_timestamp_array.length;i++){
                var temp_now_timestamp = parseInt(timestamp);
                var check_timestamp =parseInt(All_timestamp_array[i]);
                //如果陣列內存的timestamp過期了就把資料pop出去
                if(check_timestamp + windows_TO_timestamp < temp_now_timestamp){
                  All_timestamp_array.splice(i,1);
                  All_tag_array.splice(i,1);
                  if(i!=0){i-=1;}
                }
              }
              // console.log("add in " + date.format("MM/DD hh:mm:ss"));
              All_tag_array.push(tagArray);
              All_timestamp_array.push(timestamp);
              // console.log("All_timestamp_array=" +All_timestamp_array);
              // console.log(All_tag_array[0].length);
              // 可以抓到第幾項資料總共有幾個hashtag


            }

    }

    if(print==1){
      for(i=0;i<=All_tag_array.length-1;i++){
        for(var j=0;j<=All_tag_array[i].length-1;j++){
          var main_temp=All_tag_array[i][j].text;
          var related_temp = [];
          for(k=0;k<All_tag_array[i].length;k++){
            if(k!=j && main_temp!=All_tag_array[i][k].text){
              related_temp.push(All_tag_array[i][k].text);
            }
          }
          //格式變成 {main,{related,related,related...}}輸入統計資料
          if(related_temp.length !=0){

            hashtag_related(main_temp,related_temp);
          }
        }
      }
      //臨走前請清空
      // for(var t=0;t<main_hashtag.length;t++){
      //   console.log(main_hashtag[t]);
      //   console.log(related_hashtag[t]);
      // }
      sort_and_result();
      //outputTXT("*********" + date.format("MM/DD HH:mm:ss")+"*********" );

      main_hashtag=[];
      main_hashtag_count=[];
      related_hashtag=[];//二維
      related_hashtag_count=[];//二維
    }



    if(stop){
      process.exit();
    }
}


function hashtag_related(main,related){

    if(main_hashtag.length==0){
      //如果main_hashtag陣列裡面沒有值
      main_hashtag[0] = main;
      main_hashtag_count[0] = 1;
      related_hashtag[0]=[];
      related_hashtag_count[0]=[];
      for(j=0;j<related.length;j++){
        ////
        if(j==0){

          related_hashtag[0][j] = related[j];
          related_hashtag_count[0][j]=1;
        }

        else if(j!=0){
          for(var y=0;y<related_hashtag[0].length;y++){
            if(related_hashtag[0][y]!=related[j] && y==related_hashtag[0].length-1){
              related_hashtag[0][y+1] = related[j];
              related_hashtag_count[0][y+1]=1;
            }
          }
        }


      }
    }

    else if(main_hashtag.length!=0){
      //如果main_hashtag陣列裡面有值
        for(var k=0;k<main_hashtag.length;k++){
          //如果第k值的main_hashtag跟目前的main一樣
          if(main==main_hashtag[k]){
            main_hashtag_count[k]+=1;
            for(var l=0;l<related.length;l++){
              for(var h=0;h<related_hashtag[k].length;h++){
                //如果related第l值跟related_hashtag裡面的第k個main下的第h個related一樣
                if(related[l]==related_hashtag[k][h]){
                  related_hashtag_count[k][h]+=1;
                  break;
                }
                else if(related[l]!=related_hashtag[k][h] && h==related_hashtag[k].length-1){
                //如果related找不到一樣的值，建立新的related
                  related_hashtag[k][h+1]=related[l];
                  related_hashtag_count[k][h+1]=1;
                  break;
                }
              }
            }
            break;
          }

          else if (main!=main_hashtag[k] && k==main_hashtag.length-1){
            //如果找不到一樣的main，而且碰到了main_hashtag的最尾端
            // console.log("main:" + main);
            // console.log("main_hashtag[k]:" + main_hashtag[k] );
            main_hashtag[k+1] = main;
            main_hashtag_count[k+1] = 1;
            related_hashtag[k+1]=[];
            related_hashtag_count[k+1]=[];


            for(j=0;j<related.length;j++){
              //創立新的main之後把related丟進去
              if(j==0){
                related_hashtag[k+1][j] = related[j];
                related_hashtag_count[k+1][j]=1;
              }

              else if(j!=0){
                for(var y=0;y<related_hashtag[k+1].length;y++){
                  if(related_hashtag[k+1][y]!=related[j] && y==related_hashtag[k+1].length-1){
                    related_hashtag[k+1][y+1] = related[j];
                    related_hashtag_count[k+1][y+1]=1;
                    break;
                  }
                }
              }
            }
            break;
          }

        }
    }

}

function sort_and_result(){
var top=20; //只印前幾名
var countnow=0;
  for(var i=100000;i>4;i--){
    for(var k=0;k<=main_hashtag_count.length-1;k++){

      if(main_hashtag_count[k]==i && countnow!=top){
        if(check_if_target(main_hashtag[k])){
          countnow+=1;
          //outputTXT("--------------");
          //outputTXT("top:" + countnow);
          //outputTXT("TARGET HASHTAG:"+main_hashtag[k] +"  COUNTS:" +main_hashtag_count[k]);

          for(var h=10000;h>=2;h--){
            for(var u=0;u<=related_hashtag_count[k].length-1;u++){
              if(related_hashtag_count[k][u]==h){
                outputTXT(main_hashtag[k]+","+related_hashtag[k][u] + ","+
Math.floor(
(related_hashtag_count[k][u]/main_hashtag_count[k])
*1000)
/1000+ ","+ main_hashtag_count[k]+"," +related_hashtag_count[k][u],date.format("MMDD"));
               find_relateds_related(related_hashtag[k][u],1);
              }
            }
          }

        }
    }
    }
  }

    //outputTXT("ALL HASHTAG LENGTH：" + main_hashtag.length,date.format("MMDD"));
}


function find_relateds_related(main,times){
  for(var w=0;w<=main_hashtag_count.length-1;w++){
    if(main_hashtag[w]==main){
      for(var e=10000;e>=1;e--){
        for(var q=0;q<=related_hashtag_count[w].length-1;q++){
          if(related_hashtag_count[w][q]==e){
            if(times==1&&Math.floor((related_hashtag_count[w][q]/main_hashtag_count[w])*1000)/1000>0.01){
              outputTXT(main+","+related_hashtag[w][q] + "," +Math.floor((related_hashtag_count[w][q]/main_hashtag_count[w])*1000)/1000 + "," +main_hashtag_count[w]+"," +related_hashtag_count[w][q],date.format("MMDD"));

            // find_relateds_related(related_hashtag[w][q],2);
            }
            else if(times==2){
              outputTXT("------"+related_hashtag[w][q] + "COUNTS:" + related_hashtag_count[w][q]);
            }
          }
        }
      }
    }
  }

}

function check_if_target(target){
  if(
target=="サッカー"
  ){
  return 1;
  }
  else{
  return 0;
  }

}


//檢查是不是垃圾hashtag
function check_if_junk(target){
  if(
  target=="RTした人全員フォローする"
||target=="RTした人全員フォロー"
||target=="RTした人で気になった人お迎え"
||target=="RT"
||target=="拡散希望"

||target=="相互"
||target=="相互希望"
||target=="相互フォロー"
||target=="相互フォロー募集"

||target=="sougo"
||target=="sougofollow"

||target=="follow"
||target=="followme"
||target=="followmejp"
||target=="followback"
||target=="followall"
||target=="フォロー返し"
||target=="フォロー"

||target=="refollow"
||target=="リフォロー"

||target=="autofollowjp"

||target=="teamfollowback"

||target=="ドラゴンボール"
||target=="ドッカンバトル"
||target=="香るエールが飲みたい時"
||target=="メル友募集"
||target=="プレモル"
||target=="わーーーージャニオタさんと繋がるお時間がまいりましたいっぱい繋がりましょ"
||target=="モンスト"
||target=="モンストせいや"

){
  return 1;
}
else {
  return 0;
}

}


function outputTXT(string,date){

  fs.appendFileSync(date+"_logdata.txt", string+"\n", "UTF-8",{'flags': 'a+'});
  console.log("The file was saved!");

}

