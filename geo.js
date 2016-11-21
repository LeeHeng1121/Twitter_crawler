var x=0;
var count = new Array();
var sum=0;
var maxsize=0;
var minsize=9999;

exports.computeDistance = function computeDistance(tweet) {

    if(tweet.place!=null){
       if(tweet.place.country_code=='JP'){
        console.dir(tweet.place.bounding_box.coordinates,{depth:null});
        var x1 = tweet.place.bounding_box.coordinates[0][0][0];
        var y1 = tweet.place.bounding_box.coordinates[0][0][1];
        var x2 = tweet.place.bounding_box.coordinates[0][1][0];
        var y2 = tweet.place.bounding_box.coordinates[0][1][1];
        var x3 = tweet.place.bounding_box.coordinates[0][2][0];
        var y3 = tweet.place.bounding_box.coordinates[0][2][1];
        var x4 = tweet.place.bounding_box.coordinates[0][3][0];
        var y4 = tweet.place.bounding_box.coordinates[0][3][1];
        // 長乘寬
        var length = getDistanceFromLatLonInKm(y1,x1,y2,x2);
        var height = getDistanceFromLatLonInKm(y2,x2,y3,x3);
        var size = length*height;
        console.log("面積：" + size + "平方公里");
        count[x] = size;

        if(size!=0 && size<200)
        //避免過於極端的值 例如單點座標造成面積為0 或島與島之間的連線造成面積過大
        {
          if(maxsize<size){
            maxsize = size;
          }
          if(minsize > size){
            minsize = size;
          }
          // console.log(count[x-1]);
          sum += count[x];
          console.log("平均：" + sum/(x+1) + "平方公里");
          console.log("目前最大面積：" + maxsize + "平方公里");
          console.log("目前最小面積：" + minsize + "平方公里");
          console.log("目前進行到第" + x + "筆資料");
          console.log("JP");
          x+=1;
        }
        else
        {
          console.log("面積=0或過大 捨棄該資料")
        }


        // 進行到第幾筆資料之後就結束
        if(x==1000){
          process.exit();
        }
      }
    }
}

function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1);
  var a =
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ;
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c; // Distance in km
  return d;
}

function deg2rad(deg) {
  return deg * (Math.PI/180)
}
