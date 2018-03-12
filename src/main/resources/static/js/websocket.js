function connect(handlers) {
    if (!handlers) {
        handlers = [];
    }
    var socket = new SockJS("/notification");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log(frame);
        stompClient.subscribe("/user/topic/app", function (msg) {
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](JSON.parse(msg.body));
            }
        });
    });
}