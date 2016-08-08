$(function() {
	$('input[name=type]').change(function() {
		if ($('input[name=type]:checked').val() == 'ip') {
			$('#simpleip').show();
			$('#iprange').hide();
			$('input[name=ip]').prop('required', true);
			$('input[name=ip]').prop('pattern', '^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$');
			$('input[name=ip1], input[name=ip2]').prop('required', false);
			$('input[name=ip1], input[name=ip2]').prop('pattern', null);
			
		} else {
			$('#simpleip').hide();
			$('#iprange').show();
			$('input[name=ip1], input[name=ip2]').prop('required', true);
			$('input[name=ip1], input[name=ip2]').prop('pattern', '^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$');
			$('input[name=ip]').prop('required', false);
			$('input[name=ip]').prop('pattern', null);
		}
	});
	
	/*$('#allow').click(function() {
		$(this).prop('disabled', true);
		addRule('allow');
	});
	
	$('#deny').click(function() {
		$(this).prop('disabled', true);
		addRule('deny');
	});*/
	
	$('#ipform').submit(function(e) {
		e.preventDefault();
	});
	
	loadRules();
});

function loadRules() {
	var data = {};
	if (resourceId != null)
		data.id = resourceId;
	$.ajax({
		url: contextPath + '/ipaccess?action=list',
		type: 'POST',
		cache: false,
		dataType: 'json',
		data: data
	}).done(function(response) {
		$('#whiteList').empty();
		$('#blackList').empty();
		
		for (var i = 0; i < response.rules.length; i++) {
			var rule = response.rules[i];
			var option = $('<option></option>');
			option.text(rule.ip);
			option.val(rule.id);
			if (rule.type == 'white') {
				$('#whiteList').append(option);
			} else {
				$('#blackList').append(option);
			}
		}
	});
}

function addRule(action) {
	var data = {
		type: (action == 'allow' ? 2 : 1)
	};
	if (resourceId != null)
		data.id = resourceId;
	if ($('input[name=type]:checked').val() == 'ip') {
		data.ip = $('input[name=ip]').val();
	} else {
		data.ip = $('input[name=ip1]').val();
		data.ip2 = $('input[name=ip2]').val();
	}
	$.ajax({
		url: contextPath + '/ipaccess?action=add',
		type: 'POST',
		cache: false,
		dataType: 'json',
		data: data
	}).done(function(response) {
		if (response.success) {
			loadRules();
		} else {
			alert(response.error);
		}
		$('input[name=ip]').val('');
	}).always(function() {
		$('.ipaccess button').prop('disabled', false);
	});
}