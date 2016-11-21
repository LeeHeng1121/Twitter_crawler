var readline = require('linebyline'),
rl = readline('/media/neillab/yb/mongodb/bin/0613output.json',{maxLineLength:20000}),
moment = require('moment');
var mapreduce = require('mapred')(1);

//noFilterTweets 140454 count
var tweetsCount = 21216459;
// tweetsCount = 1000;
var timeMap = {};
var targetname = new Array();
var targetcount = new Array();
var last_print_time=0;
var windows_time = 30; //window長度設為幾分鐘
var windows_TO_timestamp = windows_time*=60000;
var All_tag_array;
All_tag_array=[];
var All_timestamp_array;
All_timestamp_array=[];
var i=0;j=0;
var min=0;
var before =0;
var main_hashtag=[];
var main_hashtag_count=[];
var related_hashtag=[];//二維
var related_hashtag_count=[];//二維
related_hashtag_count[0]=[];
related_hashtag_count[0][1]=1;
related_hashtag_count[1]=[];
related_hashtag_count[1][0]=1; //要宣告新的二維陣列 要重新先宣告一維在push上去

//一秒 = 1000
//一分鐘 = 60000
rl.on('line', function(eachLine, lineCount, byteCount) {
  var json = JSON.parse(eachLine);

    date = parseStringToDate(json).format("mm");

    if(last_print_time+60000<parseInt(parseStringToDate(json).format("x")) ) {
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
                if(check_timestamp + windows_TO_timestamp < parseInt(parseStringToDate(json).format("x"))){
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
      console.log(date.format("MM/DD hh:mm:ss"));
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

  for(var i=4;i<100000;i++){

    for(var k=0;k<=main_hashtag_count.length-1;k++){

      if(main_hashtag_count[k]==i){
        if(main_hashtag[k]!="RTした人全員フォローする"
          &&main_hashtag[k]!="拡散希望"
	  &&main_hashtag[k]!="相互フォロー"
	  &&main_hashtag[k]!="sougofollow"
	  &&main_hashtag[k]!="相互希望"
	  &&main_hashtag[k]!="followmejp"
	  &&main_hashtag[k]!="followme"
	  &&main_hashtag[k]!="follow"
	  &&main_hashtag[k]!="followback"
	  &&main_hashtag[k]!="sougo"
	  &&main_hashtag[k]!="リフォロー"
	  &&main_hashtag[k]!="refollow"
	  &&main_hashtag[k]!="相互"
	  &&main_hashtag[k]!="相互フォロー募集"
	  &&main_hashtag[k]!="フォロー返し"
	  &&main_hashtag[k]!="フォロー"
	  &&main_hashtag[k]!="autofollowjp"
){
          console.log("--------------");
          console.log("目標hashtag:"+main_hashtag[k] +"次數:" +main_hashtag_count[k]);
          console.log("關聯hashtag:");

          for(var h=10000;h>=0;h--){
            for(var u=0;u<=related_hashtag_count[k].length-1;u++){
              if(related_hashtag_count[k][u]==h){
                console.log(related_hashtag[k][u] + "次數:" + related_hashtag_count[k][u]);
              }
            }
          }
        }

      }
    }
  }


    console.log("總數目：" + main_hashtag.length);

}
