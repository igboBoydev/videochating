const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');

let localStream;
let peerConnection;
let ws;

function connect() {
    ws = new WebSocket('ws://localhost:8091/ws/video-call');

    ws.onopen = function() {
        console.log("WebSocket connection established.");
    };

    ws.onmessage = function(event) {
        console.log("Received message: ", event.data);
    };

    ws.onclose = function(event) {
        console.log("WebSocket closed. Reconnecting in 3 seconds...");
        setTimeout(connect, 3000);  // Try to reconnect after 3 seconds
    };

    ws.onerror = function(error) {
        console.error("WebSocket error: ", error);
    };
}

// Initialize WebSocket connection
connect();

setInterval(function() {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send("ping");
    }
}, 3000);  // Send a "ping" every 30 seconds

const configuration = {
    iceServers: [
        {
            urls: 'stun:stun.l.google.com:19302'
        }
    ]
};

// Get the local video and audio stream
navigator.mediaDevices.getUserMedia({ video: true, audio: true })
    .then(stream => {
        localVideo.srcObject = stream;
        localStream = stream;
    })
    .catch(error => {
        console.error('Error accessing media devices.', error);
    });

// Create an RTCPeerConnection when a user joins the call
ws.onmessage = (message) => {
console.log({message})
    const data = JSON.parse(message.data);

    switch (data.type) {
        case 'offer':
            handleOffer(data.offer);
            break;
        case 'answer':
            handleAnswer(data.answer);
            break;
        case 'candidate':
            handleCandidate(data.candidate);
            break;
        default:
            break;
    }
};

// Create an offer to start the call
function createOffer() {
    peerConnection = new RTCPeerConnection(configuration);

    // Add the local stream to the connection
    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

    peerConnection.ontrack = (event) => {
        remoteVideo.srcObject = event.streams[0];  // Display the remote video
    };

    // ICE candidate handling
    peerConnection.onicecandidate = (event) => {
        if (event.candidate) {
            ws.send(JSON.stringify({
                type: 'candidate',
                candidate: event.candidate
            }));
        }
    };

    peerConnection.createOffer()
        .then(offer => {
            peerConnection.setLocalDescription(offer);
            ws.send(JSON.stringify({
                type: 'offer',
                offer: offer
            }));
        });
}

// Handle the offer from the remote peer
function handleOffer(offer) {
    peerConnection = new RTCPeerConnection(configuration);
    peerConnection.setRemoteDescription(new RTCSessionDescription(offer));

    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

    peerConnection.ontrack = (event) => {
        remoteVideo.srcObject = event.streams[0];
    };

    peerConnection.onicecandidate = (event) => {
        if (event.candidate) {
            ws.send(JSON.stringify({
                type: 'candidate',
                candidate: event.candidate
            }));
        }
    };

    peerConnection.createAnswer()
        .then(answer => {
            peerConnection.setLocalDescription(answer);
            ws.send(JSON.stringify({
                type: 'answer',
                answer: answer
            }));
        });
}

// Handle the answer from the remote peer
function handleAnswer(answer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
}

// Handle ICE candidates from the remote peer
function handleCandidate(candidate) {
    peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
}

function toggleCamera() {
    const videoTrack = localStream.getVideoTracks()[0];

    if (videoTrack.enabled) {
        videoTrack.enabled = false;  // Hide the camera (disable video track)
        console.log("Camera is turned off.");
    } else {
        videoTrack.enabled = true;   // Show the camera (enable video track)
        console.log("Camera is turned on.");
    }
}

function toggleAudio() {
    const audioTrack = localStream.getAudioTracks()[0];

    if (audioTrack.enabled) {
        audioTrack.enabled = false;  // Mute the microphone
        console.log("Audio is muted.");
    } else {
        audioTrack.enabled = true;   // Unmute the microphone
        console.log("Audio is unmuted.");
    }
}

// Call this function to start the call when the user clicks "Start"
function startCall() {
    createOffer();
}
