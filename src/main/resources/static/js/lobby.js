// Mock data for demonstration
const difficultyLabels = ['Easy', 'Medium', 'Hard', 'Extreme'];
const durationLabels = ["Quick Game", "Standard", "Marathon", "No Time Limit"];

function updateDifficulty(value) {
    const label = difficultyLabels[value];
    document.getElementById('difficultyValue').textContent = label;
    if (document.getElementById('mobileDifficultyValue')) {
        document.getElementById('mobileDifficultyValue').textContent = label;
    }

    // Send to server
    // fetch('/api/lobby/settings', { method: 'POST', body: JSON.stringify({difficulty: value}) });
}

function updateDuration(value) {
    const label = durationLabels[value];
    document.getElementById('durationValue').textContent = label;
    if (document.getElementById('mobileDurationValue')) {
        document.getElementById('mobileDurationValue').textContent = label;
    }

    // Send to server
    // fetch('/api/lobby/settings', { method: 'POST', body: JSON.stringify({duration: value}) });
}

function vote(setting, action) {
    // Remove previous selection
    document.querySelectorAll('.vote-btn').forEach(btn => btn.classList.remove('selected'));

    // Add selection to clicked button
    event.target.classList.add('selected');

    // Add message to chat
    const message = document.createElement('div');
    message.className = 'message user';
    message.textContent = `🗳️ You voted to ${action} ${setting}`;

    const containers = document.querySelectorAll('.messages-container');
    containers.forEach(container => {
        container.appendChild(message.cloneNode(true));
        container.scrollTop = container.scrollHeight;
    });

    // Send to server
    // fetch('/api/lobby/vote', { method: 'POST', body: JSON.stringify({setting, action}) });
}

function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Show selected tab
    document.getElementById(tabName + 'Tab').classList.add('active');
    event.target.classList.add('active');
}

// Auto-scroll messages to bottom
document.querySelectorAll('.messages-container').forEach(container => {
    container.scrollTop = container.scrollHeight;
});
