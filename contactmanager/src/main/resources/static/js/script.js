const toggleSidebar = () => {

    if ($(".sidebar").is(":visible")) {
        
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");

    } else {

        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");

    }

};

const searchContacts = () => {

    //console.log("searching...");

    let searchKey = $("#search-input").val();

    if(searchKey != '') {

        //console.log(searchKey);
        
        let url = `http://localhost:8282/search/${searchKey}`;
        
        fetch(url)
        	.then((response) =>{
				return response.json();
			})
			.then((data) => {
				//console.log(data);

                let text = `<div class='list-group'>`;

                data.forEach((contact) => {

                    text += `<a href='/user/contact-detail/${contact.contactId}' class='list-group-item list-group-item-action'> ${contact.fullName} </a>`

                });

                text += `</div>`;

                $(".search-result").html(text);

                $(".search-result").show();

			});

    }
    else {
        $(".search-result").hide();
    }

};