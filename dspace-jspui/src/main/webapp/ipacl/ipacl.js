var selectedIp = null;

$(function() {
	$('#deletebtn').click(deleteRule);
	
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
	
	$('#whiteList').change(function(e) {
		if ($(this).find('option:selected').length == 0)
			return;
		
		$('#blackList option').prop('selected', false);
	});
	
	$('#blackList').change(function(e) {
		if ($(this).find('option:selected').length == 0)
			return;
		
		$('#whiteList option').prop('selected', false);
	});
	
	$('#whiteList, #blackList').change(function() {
		var option = $(this).find('option:selected');
		if (option.length == 0)
			return;
		
		selectedIp = option.val();
		$('.ipaccess .controls').show();
	});
	
	$('#ipform').submit(function(e) {
		e.preventDefault();
		var btn = $('.ipaccess button[clicked=true]');
		btn.prop('disabled', true);
		addRule(btn.val());
	});
	
	$('.ipaccess button').click(function() {
		$('.ipaccess button').removeAttr('clicked');
	    $(this).attr('clicked', 'true');
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
		$('.ipaccess .controls').hide();
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
			$('input[name=ip], input[name=ip1], input[name=ip2]').val('');
			loadRules();
		} else {
			alert(response.error);
		}
	}).always(function() {
		$('.ipaccess button').prop('disabled', false);
	});
}

function deleteRule(e) {
	e.preventDefault();
	if (!window.confirm('Вы действительно хотите удалить ip адрес?'))
		return;
	
	$.ajax({
		url: contextPath + '/ipaccess?action=delete',
		type: 'POST',
		cache: false,
		dataType: 'json',
		data: {
			id: selectedIp
		}
	}).done(function(response) {
		loadRules();
	});
}