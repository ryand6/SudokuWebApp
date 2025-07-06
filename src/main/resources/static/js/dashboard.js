// Dark mode toggle
function toggleTheme() {
    document.documentElement.classList.toggle('dark');
    localStorage.setItem('theme', document.documentElement.classList.contains('dark') ? 'dark' : 'light');
}

// Load theme on page load
(function () {
    if (localStorage.getItem('theme') === 'dark') {
        document.documentElement.classList.add('dark');
    }
})();

// Set variables for handling modal
let lobbyResultsPage = 0;
let isLoading = false;
let publicSelected = true;

// Define function for getting list of 10x available lobbies
function loadPublicLobbies(reset) {
  if (reset) {
    document.getElementById('modalRightPane').innerHTML = '';
    lobbyResultsPage = 0;
  }
  isLoading = true;
  fetch(`/lobby/public/get-active-lobbies?page=${page}`)
    .then(res => res.json())
    .then(data => {
      if (data.length === 0) return;
      const pane = document.getElementById('modalRightPane');
      // Create entry row for each lobby returned
      data.forEach(lobby => {
        const lobbyEntryContainer = document.createElement('div');
        lobbyEntryContainer.classList.add('lobby-entry-container');
        const lobbyEntryNameDiv = document.createElement('div');
        lobbyEntryNameDiv.classList.add('lobby-entry-name');
        const lobbyEntryRightHandDiv = document.createElement('div');
        lobbyEntryRightHandDiv.classList.add('lobby-entry-right-hand-div');
        const lobbyEntryPlayerCountDiv = document.createElement('div');
        lobbyEntryPlayerCountDiv.classList.add('lobby-entry-player-count');
        const lobbyEntryJoinButtonDiv = document.createElement('div');
        lobbyEntryJoinButtonDiv.classList.add('lobby-entry-join-button-container');
        lobbyEntryRightHandDiv.appendChild(lobbyEntryPlayerCountDiv);
        lobbyEntryRightHandDiv.appendChild(lobbyEntryJoinButtonDiv);
        // Calls LobbyController method to attempt to join lobby
        const joinLobbyButton = document.createElement('a');
        joinLobbyButton.href = `/lobby/public/join/${lobby.id}`;
        joinLobbyButton.textContent = "Join Lobby";
        joinLobbyButton.className = "button-link";
        // Display name of lobby
        lobbyEntryNameDiv.textContent = `${lobby.lobbyName}`;
        // Show count of active players in lobby
        lobbyEntryPlayerCountDiv.textContent = `${lobby.users.length}/4 players`;
        lobbyEntryContainer.appendChild(lobbyEntryNameDiv);
        lobbyEntryContainer.appendChild(lobbyEntryRightHandDiv);
        pane.appendChild(lobbyEntryContainer);
      });
      // Increment page so that next 10x results are displayed when current 10x have been scrolled through
      lobbyResultsPage++;
      // Reset loading variable once results have been displayed
      isLoading = false;
    });
}

// Create event listener to open the modal window when the Join Lobby button is pressed
document.getElementById('openLobbyModalBtn').addEventListener('click', () => {
  document.getElementById('lobbyModal').classList.remove('hidden');
  document.body.classList.add('modal-open');
  loadPublicLobbies(true);
});

// Close modal and unblur background when clicked
document.getElementById('modalCloseBtn').addEventListener('click', () => {
  document.getElementById('lobbyModal').classList.add('hidden');
  document.body.classList.remove('modal-open');
})

// Handle switching to Public Lobby tab, showing the first page of results
document.getElementById('publicTab').addEventListener('click', () => {
  publicSelected = true;
  lobbyResultsPage = 0;
  loadPublicLobbies(true);
});

// Handle switching to Private Lobby tab, displaying input for join code
document.getElementById('privateTab').addEventListener('click', () => {
  publicSelected = false;
  document.getElementById('modalRightPane').innerHTML = `
    <input type="text" id="joinCodeInput" placeholder="Enter join code" />
    <button id="joinPrivateBtn">Join</button>
  `;
});

// Infinite scroll mechanic for public lobbies list
modalRightPane.addEventListener('scroll', () => {
  const pane = document.getElementById('modalRightPane');
  // Check that public tab is selected, lobbies aren't currently being loaded, and the window has been scrolled to within 10px of the bottom
  if (publicSelected && !isLoading && pane.scrollTop + pane.clientHeight >= pane.scrollHeight - 10) {
    loadPublicLobbies(false);
  }
});

// Event listener to handle requesting to join private lobby by means of input join code
document.addEventListener('click', (e) => {
  // e.target used because joinPrivateBtn is injected into HTML, therefore can't bind a normal event listener if the HTML doesn't exist (private tab not selected)
  if (e.target && e.target.id === 'joinPrivateBtn') {
    const joinCode = document.getElementById('joinCodeInput').value;
    // Send data via request body
    fetch(`/lobby/join`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ joinCode })
    })
    .then(response => {
      if (response.ok) {
        alert("Successfully joined the lobby!");
        // redirect or update UI
      } else {
        alert("Invalid join code or lobby not found.");
      }
      // Add logic for handling if lobby is full
    });
  }
});


