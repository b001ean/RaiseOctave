// Misc. scripts

// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function() { scrollFunction() };

function scrollFunction() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        document.getElementById("toTop").style.display = "block";
    } else {
        document.getElementById("toTop").style.display = "none";
    }
};

// When the user clicks on the button, scroll to the top of the document
function topFunction() {
    //document.body.scrollTop= 0; // For Chrome, Safari and Opera
    //document.documentElement.scrollTop = 0; // For IE and Firefox
     $('html, body').animate({scrollTop:0}, 'slow');
};

// Preview URL for image posting
function readURL(input) {
    $('#previewImgText').html("Preview Image: ");
    $('#previewImg').attr('src', input);
}
