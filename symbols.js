var i=0;
var i1=0;
var i2=0;
var x=0;
var y=0;
var count = new Array();
var stockname = new Array();
count[0] =0;
var sum=0;
var Str = "";
var entry = new Array();
var userid ="";
var user_and_symbol = Array();

exports.CountSymbols = function CountSymbols(tweet) {
  if(tweet.entities!=null && tweet.user!=null){
    if (tweet.entities.symbols.length !=0) {
      userid=tweet.user.name;
      Str=tweet.entities.symbols;
      user_and_symbol[y] = userid;
      for(i2=0;i2<Str.length;i2++){
        user_and_symbol[y] += ','+Str[i2].text ;
         for(i=0;i<=x;i++){
            if(stockname[i]==Str[i2].text){
             count[i] +=1;
             y+=1;
             break;
            }
            else if(i==x && stockname[i] != Str[i2].text){
              stockname[x]=Str[i2].text;
              count[x]=1;
              y+=1;
              x+=1;
              break;
            }
          }

      }


          // console.log(Str[0]);
          // console.log(Str.length);
          // console.log(Str[0].text);
          // console.log("拆開");
          // console.dir(stockname[0],{depth:null});
          for(i1=0;i1<x;i1++){
            console.log(stockname[i1] +"次數:" + count[i1]);
          }
          for(i1=0;i1<y;i1++){
            console.log(user_and_symbol[i1]);
          }

          console.log("------------");
      }
    }
  }
