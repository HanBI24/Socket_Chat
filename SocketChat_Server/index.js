const express = require('express')
const path = require('path')
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.set('port', (process.env.PORT || 5000));

app.use(express.static(__dirname + '/public'));

// views is directory for all template files
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');

app.get('/', function (req, res) {
  res.send("hello chat")
})

//서버연결하기 직전에뜸
console.log("outside io");

// 통신 관련
io.on('connection', function (socket) {

  //로그인하면 이거 밑에 두개뜸
  console.log('User Conncetion');

  // 수신
  // socket.on: Client에서 "connect user"로 request하면
  // 함께 보내온 데이터를 갖고 로그를 찍거나 다시 emit을 통해 클라이언트로 응답을 보냄
  // user: 안스 userId, userId에 넣어놓은 roomName은 user['roomName']
  socket.on('connect user', function (user) {
    console.log("Connected user ");
    // 채팅방 참가
    socket.join(user['roomName']);
    console.log("roomName : ", user['roomName']);
    // 어떤 방에 누가 참여했는지
    console.log("state : ", socket.adapter.rooms);
    // io.emit: "이벤트 이름"으로 Client에 data를 담아서 response
    // 서버에서 요청을 받았으니 클라이언트(안스)에서 connect user emit()을 실행해 주겠다는 의미 
    // => mSocket.on("connect user", onNewUser)
    io.emit('connect user', user);
  });

  //타이핑중에 이거뜸
  socket.on('on typing', function (typing) {
    console.log("Typing.... ");
    io.emit('on typing', typing);
  });

  //mSocket.on("chat message", onNewMessage)
  //메세지 입력하면 서버 로그에 이거뜸
  socket.on('chat message', function (msg) {
    console.log("Message " + msg['message']);
    console.log("보내는 메세지 : ", msg['roomName']);
    io.to(msg['roomName']).emit('chat message', msg);
  });
});

//맨 처음에 서버 연결하면 몇번포트에 서버 연결되어있는지 
http.listen(app.get('port'), function () {
  console.log('Node app is running on port', app.get('port'));
});

