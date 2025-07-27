// get first checkbox element
const publicCheckbox = document.getElementById("public-checkbox");
// get second checkbox element
const privateCheckbox = document.getElementById("private-checkbox");
const joinCodeContainer = document.getElementById('join-code-container');
const joinCodeInput = document.getElementById('join-code');

// used to toggle between private and public checkboxes, only one can be selected at a time
function toggleCheck(source, target) {
source.addEventListener('change', () => {
  if (source.checked) {
    target.checked = false;
    }
  });
}

toggleCheck(publicCheckbox, privateCheckbox);
toggleCheck(privateCheckbox, publicCheckbox);
