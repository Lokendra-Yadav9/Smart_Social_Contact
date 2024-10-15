$(document).ready(function() {
	var trigger = $('.hamburger'),
		overlay = $('.overlay'),
		isClosed = true;

	trigger.click(function() {
		hamburger_cross();
	});

	function hamburger_cross() {
		if (isClosed) {
			overlay.show();
			console.log("its woking");
			trigger.removeClass('is-closed').addClass('is-open');
			$('#wrapper').addClass('toggled');
			isClosed = false;
		} else {
			overlay.hide();
			trigger.removeClass('is-open').addClass('is-closed');
			$('#wrapper').removeClass('toggled');
			isClosed = true;
		}
	}

	$('.overlay').click(function() {
		console.log("its woking");
		hamburger_cross();
	});

	$('#sidebar-toggle').click(function() {
		console.log("its woking");
		hamburger_cross();
	});
});



function confirmDeleteAllContacts(element) {
	const userId = element.getAttribute('data-user-id');

	console.log("UserId:", userId); // Verify the value in the console

	// Check if userId is valid
	if (!userId || isNaN(userId)) {
		console.error("Invalid UserId:", userId);
		return;
	}

	const deleteUrl = `/user/delete-all/${userId}`;
	console.log("Delete URL:", deleteUrl); // Verify the URL in the console

	Swal.fire({
		title: 'Are you sure?',
		text: 'Once deleted, you will not be able to recover this data!',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Yes, delete it!',
		cancelButtonText: 'No, cancel!',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {
			// Redirect to the delete URL
			window.location.href = deleteUrl;
		} else if (result.dismiss === Swal.DismissReason.cancel) {
			Swal.fire('Your data is safe!');
		}
	});
}



function confirmDeleteContact(contactId, currentPage, pageSize) {
	// Validate parameters
	if (!contactId) {
		console.error("Contact ID is missing.");
		return;
	}
	if (currentPage === undefined || pageSize === undefined) {
		console.error("Current page or page size is missing.");
		return;
	}

	// Construct the delete URL
	const deleteUrl = `/user/delete/${contactId}?page=${currentPage}&pageSize=${pageSize}`;
	console.log("Delete URL:", deleteUrl); // Verify the URL in the console

	// Check if Swal is defined
	if (typeof Swal === 'undefined') {
		console.error("SweetAlert2 (Swal) is not defined. Please include the SweetAlert2 library.");
		return;
	}

	Swal.fire({
		title: 'Are you sure?',
		text: 'Once deleted, you will not be able to recover this contact!',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Yes, delete it!',
		cancelButtonText: 'No, cancel!',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {
			// Redirect to the delete URL
			window.location.href = deleteUrl;
		} else if (result.dismiss === Swal.DismissReason.cancel) {
			Swal.fire('Your contact is safe!');
		}
	});
}








/*   contact update image name show code */

/*  image preview code in the contact update page */
document.addEventListener('DOMContentLoaded', function() {
	const contactImage = document.querySelector('.contact-image');
	const inputImage = document.getElementById('Inputimage');
	const fileNameDisplay = document.getElementById('file-name');
	const previewImage = document.getElementById('previewImage');
	const imagePreviewModal = new bootstrap.Modal(document.getElementById('imagePreviewModal'));

	inputImage.addEventListener('change', function() {
		const fileName = this.files.length > 0 ? this.files[0].name : 'No file chosen';
		fileNameDisplay.textContent = fileName;
	});

	contactImage.addEventListener('click', function() {
		// Assuming 'th:src' has been processed server-side and rendered into a proper 'src' attribute
		const src = this.getAttribute('src');
		previewImage.setAttribute('src', src);
		imagePreviewModal.show();
	});
});



document.addEventListener('DOMContentLoaded', function() {
	const contactImage = document.querySelector('.contact-image');
	const inputImage = document.getElementById('inputContactImage');
	const fileNameDisplay = document.getElementById('file-name-contact');
	const previewImage = document.getElementById('contactPreviewImage');
	const imagePreviewModal = new bootstrap.Modal(document.getElementById('contactImagePreviewModal'));

	inputImage.addEventListener('change', function() {
		const fileName = this.files.length > 0 ? this.files[0].name : 'No file chosen';
		fileNameDisplay.textContent = fileName;
	});

	contactImage.addEventListener('click', function() {
		const src = this.getAttribute('src');
		previewImage.setAttribute('src', src);
		imagePreviewModal.show();
	});
});

const search = () => {
	console.log("Searching...");

	let query = $("#search-input").val().trim();

	if (query === "") {
		$(".search-contact").hide();
	} else {
		console.log("Query:", query);

		// Ensure query is URL-encoded
		let url = `http://localhost:8080/search/${encodeURIComponent(query)}`;
		console.log("URL:", url);

		fetch(url)
			.then((response) => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();
			})
			.then((data) => {

				if (Array.isArray(data) && data.length > 0) {
					let text = `<div class='list-group'>`;
					data.forEach((contact) => {
						text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>
                                    ${contact.cname}
                                 </a>`;
					});
					text += `</div>`;

					$(".search-contact").html(text).show();
				} else {
					$(".search-contact").html("<p>No contacts found.</p>").show();
				}
			})
			.catch((error) => {
				console.error('There was a problem with the fetch operation:', error);
				$(".search-contact").html("<p>Error retrieving contacts. Please try again later.</p>").show();
			});
	}
}


$(document).ready(function() {
	$('.custom-nav-item').on('click', function(e) {
		e.preventDefault();
		$('.custom-nav-item').removeClass('active');
		$(this).addClass('active');

		var target = $(this).attr('href');
		$('.tab-pane').removeClass('active show');
		$(target).addClass('active show');
	});
});
