'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var chatBoxTitle = document.querySelector('#chat-channel');

var stompClient = null;
var username = null;
var channel = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    channel = document.querySelector('#channel').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/wkws');
        stompClient = Stomp.over(socket);

        // stompClient.debug = null;

        stompClient.connect({
            sender: username,
            channel: channel
        }, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {

    chatBoxTitle.innerHTML += ' ' + channel;

    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public/' + channel, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.register/" + channel,
        {},
        JSON.stringify({sender: username, channel: channel, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function send(event) {
    var messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            channel: channel,
            unixTimestamp: Math.round((new Date()).getTime() / 1000),
            content: messageInput.value,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.send/" + channel, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');


    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    var timestampElement = document.createElement('div');
    timestampElement.classList.add('message-date');
    timestampElement.append(convertTimeStamp(message.unixTimestamp));

    messageElement.appendChild(timestampElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function convertTimeStamp(timestamp) {
    // Create a new JavaScript Date object based on the timestamp
    // multiplied by 1000 so that the argument is in milliseconds, not seconds.
    var date = new Date(timestamp * 1000);
    // Year part from the timestamp
    var year = date.getFullYear();
    //Month part from the timestamp
    var month = date.getMonth();
    //Day part of the timestamp
    var day = date.getDay();
    // Hours part from the timestamp
    var hours = date.getHours();
    // Minutes part from the timestamp
    var minutes = "0" + date.getMinutes();
    // Seconds part from the timestamp
    var seconds = "0" + date.getSeconds();

    // Will display time in 10:30:23 format
    var formattedTime = year + '-' + month + '-' + day + '  ' + hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);

    return formattedTime;
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', send, true);