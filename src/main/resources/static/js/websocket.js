function connect(handlers) {
    if (!handlers) {
        handlers = {};
    }
    var socket = new SockJS("/notification");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/topic/app", function (msg) {
            var msgJSON = JSON.parse(msg.body);
            var type = msgJSON.type;
            var content = msgJSON.content;
            var handler = handlers[type];
            if (handler) {
                handler(content);
            }
        });
    });
}