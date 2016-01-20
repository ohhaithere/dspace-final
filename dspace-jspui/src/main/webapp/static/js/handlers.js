$( document ).ready(function() {

    $("#metadata_check_all").click(function( event ) {
        var arr = [];
        $('.exportThisItem').each(function(i, obj) {
            if(obj.checked){
                arr.push(obj.id);
             }
        });

        $.ajax({
            async: false,
            type: "POST",
            contentType: "application/json",
            url: "browse?type=title&submit_export_metadata=Export+metadata",
            data: JSON.stringify(arr),
            dataType : "json",
            success: function(response){
            }
        });
	alert("Данные выгружены");
        return false;
    });

 $("#metadata_full_omg").click(function( event ) {
        var arr = [];
        $('.exportThisItem').each(function(i, obj) {

                obj.checked = true;

        });


    });

     $( "#metadata_import_omg" ).click(function( event ) {

	var value = $("#metadata_import_val").val();
            if(value.length == 0){
                alert('Идентификатор пустой');
		        return false;
            }

        });

 $("#metadata_import_name_omg").click(function( event ) {

	var value1 = $("#author_name").val();
	var value2 = $("#import_name").val();
            if((value1.length == 0)&&(value2.length == 0)){
                alert('Поля пустые');
		        return false;
            }

        });

});