$( document ).ready(function() {

    $("#dateMask1").datepicker({dateFormat: 'yy-mm-dd'});
    $("#dateMask2").datepicker({dateFormat: 'yy-mm-dd'});

    $("#send_dates").click(function( event ) {

            	var value1 = $("#dateMask1").val();
            	var value2 = $("#dateMask2").val();
                        if((value1.length == 0)||(value2.length == 0)){
                            alert('Не все даты заполнены!');
            		        return false;
                        }
			if(value2 < value1){
				alert("Дата 'C' больше даты 'По'");
				return false;
			}

                    });

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
            url: "browse?type=title&submit_export_metadata=Export+metadata&items="+JSON.stringify(arr)+"&system_to="+$( "#system_to option:selected" ).val(),
            data: {
            items : JSON.stringify(arr),
            path : $( "#system_to option:selected" ).text()
            },
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

  $("#metadata_full_wtf").click(function( event ) {
         var arr = [];
         $('.exportThisItem').each(function(i, obj) {

                 obj.checked = false;

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

        $("#metadata_import_name_wtf").click(function( event ) {

        	var value1 = $("#author_name").val();
        	var value2 = $("#import_name").val();
                    if((value1.length == 0)||(value2.length == 0)){
                        alert('Поля пустые');
        		        return false;
                    }

                });

  $("#button_spin").click(function( event ) {
var docHeight = $(document).height();

   $("body").append("<div id='overlay'></div>");

   $("#overlay")
      .height(docHeight)
      .css({
         'opacity' : 0.4,
         'position': 'absolute',
         'top': 0,
         'left': 0,
         'background-color': 'black',
         'width': '100%',
         'z-index': 5000
      });
var opts = {
  lines: 13 // The number of lines to draw
, length: 24 // The length of each line
, width: 14 // The line thickness
, radius: 28 // The radius of the inner circle
, scale: 1 // Scales overall size of the spinner
, corners: 1 // Corner roundness (0..1)
, color: '#000' // #rgb or #rrggbb or array of colors
, opacity: 0.25 // Opacity of the lines
, rotate: 0 // The rotation offset
, direction: 1 // 1: clockwise, -1: counterclockwise
, speed: 1 // Rounds per second
, trail: 60 // Afterglow percentage
, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
, zIndex: 2e9 // The z-index (defaults to 2000000000)
, className: 'spinner' // The CSS class to assign to the spinner
, top: '50%' // Top position relative to parent
, left: '50%' // Left position relative to parent
, shadow: false // Whether to render a shadow
, hwaccel: false // Whether to use hardware acceleration
, position: 'absolute' // Element positioning
}
var target = document.getElementById('wow-spinner')
var spinner = new Spinner(opts).spin(target);


          });

$(document).on("click", "a.deleteText", function() {
    if (!confirm('Вы действительно хотите удалить позицию?')) {
        return false;
    }
});

});