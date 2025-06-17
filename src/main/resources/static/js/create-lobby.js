// get first checkbox element
const publicCheckbox = document.getElementByID( "public-checkbox");
// get second checkbox element
const privateCheckbox = document.getElementByID( "private-checkbox" );
const joinCodeContainer = document.getElementById('join-code-container');
const joinCodeInput = document.getElementById('join-code');

// used to toggle between private and public checkboxes, only one can be selected at a time
function toggleCheck(source, target) {
source.addEventListener('change', () => {
  if (source.checked) {
    target.checked = false;
    updateFormState();
    }
  });
}

// hide or unhide joincode box if public or private is checked
// if private is checked, create a unique join code automatically and present it to the user
function updateFormState() {
    if (privateCheckbox.checked) {
      joinCodeContainer.classList.remove('hidden');
      joinCodeInput.setAttribute('required', 'required');
      fetch('/lobby/generate-join-code')
        .then(res => res.text())
        .then(code => joinCodeInput.value = code);
    } else {
      joinCodeContainer.classList.add('hidden');
      joinCodeInput.value = '';
      joinCodeInput.removeAttribute('required');
    }
}

toggleCheck(publicCheckbox, privateCheckbox);
toggleCheck(privateCheckbox, publicCheckbox);
