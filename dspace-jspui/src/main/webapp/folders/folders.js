window.folders = {};
var selectedFolder = null;

$.fn.filterByData = function(prop, val) {
    return this.filter(
        function() { return $(this).data(prop)==val; }
    );
};

$(function() {
	$('#folder_form').submit(saveImportFolder);
	$('#backbtn').click(function(e) {
		resetFolderForm();
	});
	$('#deletebtn').click(deleteFolder);
	$('#runbtn').click(runFolder);
	$('#folder_form input').keyup(setFormModified);
	$('#folder_form select').change(setFormModified);
	$(document).on('click', '#folder_list a', onFolderSelect);
	loadImportFolders();
	setInterval(loadImportFolders, 30000);
});

function setFormModified() {
	$('#folder_form').addClass('modified');
}

function loadImportFolders() {
	$.ajax({
		url: contextPath + '/fold?action=list',
		type: 'GET',
		dataType: 'json',
		cache: false
	}).done(function(response) {
		window.folders = response;
		var container = $('#folder_list');
		container.empty();
		for (var id in response) {
			var folder = response[id];
			var a = $('<a href="#"><i class="glyphicon glyphicon-folder-open"></i> <span></span></a>');
			a.data('id', id);
			a.find('span').text(folder.path);
			container.append(a);
		}
		
		if (selectedFolder != null) {
			$('#folder_list a').filterByData('id', selectedFolder).addClass('selected');
			if (response[selectedFolder].alive) {
				$('.folder-actions #runbtn').hide();
				$('.folder-actions #progress').show();
			} else {
				$('.folder-actions #progress').hide();
				$('.folder-actions #runbtn').show();
			}
		}
	});
}

function resetFolderForm(force) {
	var form = $('#folder_form');
	if (force !== true) {
		var filledFields = getFilledFields();
		if (filledFields.length > 0 && form.hasClass('modified')) {
			if (!window.confirm("У Вас есть несохраненное расписание.\nПерейти к созданию?"))
				return;
		}
	}
	$('#error').empty();
	form.removeClass('modified');
	form.get(0).reset();
	form.find('input[name=id]').val('');
	$('#savebtn').text('Создать');
	$('#backbtn').hide();
	$('.folder-actions').hide();
	$('#folder_list a').removeClass('selected');
	selectedFolder = null;
}

function saveImportFolder(e) {
	e.preventDefault();
	var hour = parseInt($('#folder_form input[name=hour]').val());
	var minute = parseInt($('#folder_form input[name=minute]').val());
	if ((hour != NaN && (hour < 0 || hour > 23)) || (minute != NaN && (minute < 0 || minute > 59))) {
		alert('Время указано некорректно');
		return;
	}
	
	$('#savebtn').prop('disabled', true);
	$('#error').empty();
	$.ajax({
		url: contextPath + '/fold',
		type: 'POST',
		dataType: 'json',
		data: $('#folder_form').serialize()
	}).done(function(response) {
		if (response.success) {
			resetFolderForm(true);
			loadImportFolders();
		} else {
			$('#error').text(response.error);
		}
	}).always(function() {
		$('#savebtn').prop('disabled', false);
	});
}

function getFilledFields() {
	return $('#folder_form').find('input[type=number], input[type=text]').filter(function() {
        return ($(this).val().length > 0);
    });
}

function onFolderSelect(e) {
	e.preventDefault();
	var form = $('#folder_form');
	var filledFields = getFilledFields();
	if (filledFields.length > 0 && form.hasClass('modified')) {
		if (!window.confirm("У Вас есть несохраненное расписание.\nПерейти к редактированию?"))
			return;
	}
	$('#error').empty();
	form.removeClass('modified');
	$('#folder_list a').removeClass('selected');
	$(this).addClass('selected');
	var id = $(this).data('id');
	var folder = window.folders[id];
	form.find('input[name=id]').val(id);
	if (folder.hour != null) {
		form.find('input[name=hour]').val(pad(folder.hour, 2));
	} else {
		form.find('input[name=hour]').val('');
	}
	if (folder.minute != null) {
		form.find('input[name=minute]').val(pad(folder.minute, 2));
	} else {
		form.find('input[name=minute]').val('');
	}
	form.find('select[name=date]').val(folder.date);
	form.find('select[name=month]').val(folder.month);
	form.find('select[name=year]').val(folder.year);
	form.find('select[name=day]').val(folder.weekday);
	form.find('input[name=path]').val(folder.path);
	$('#savebtn').text('Сохранить');
	$('#backbtn').show();
	$('.folder-actions').show();
	if (folder.alive) {
		$('.folder-actions #runbtn').hide();
		$('.folder-actions #progress').show();
	} else {
		$('.folder-actions #progress').hide();
		$('.folder-actions #runbtn').show();
	}
	selectedFolder = id;
}

function deleteFolder(e) {
	e.preventDefault();
	if (!window.confirm('Вы действительно хотите удалить папку?'))
		return;
	
	$.ajax({
		url: contextPath + '/fold?action=delete',
		type: 'GET',
		data: {id: $('#folder_form').find('input[name=id]').val()},
		dataType: 'json',
		cache: false
	}).done(function(response) {
		resetFolderForm(true);
		loadImportFolders();
	});
}

function runFolder(e) {
	e.preventDefault();
	if (!window.confirm('Вы действительно хотите запустить импорт?'))
		return;
	
	$.ajax({
		url: contextPath + '/fold?action=run',
		type: 'GET',
		data: {id: $('#folder_form').find('input[name=id]').val()},
		dataType: 'json',
		cache: false
	}).done(function(response) {
		if (response.success) {
			loadImportFolders();
			$('.folder-actions #runbtn').hide();
			$('.folder-actions #progress').show();
		} else {
			alert(response.error);
		}
	});
}

function pad(num, size) {
	var s = num+"";
	while (s.length < size) s = "0" + s;
	return s;
}