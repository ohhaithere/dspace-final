window.folders = {};

$(function() {
	$('#folder_form').submit(saveImportFolder);
	$('#backbtn').click(function(e) {
		resetFolderForm();
	});
	$('#deletebtn').click(deleteFolder);
	$(document).on('click', '#folder_list a', onFolderSelect);
	loadImportFolders();
});

function loadImportFolders() {
	$.ajax({
		url: contextPath + '/fold?action=list',
		type: 'GET',
		dataType: 'json'
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
	});
}

function resetFolderForm(force) {
	if (force !== true) {
		var filledFields = getFilledFields();
		if (filledFields.length > 0) {
			if (!window.confirm("У Вас есть несохраненное расписание.\nПерейти к созданию?"))
				return;
		}
	}
	var form = $('#folder_form');
	form.get(0).reset();
	form.find('input[name=id]').val('');
	$('#savebtn').text('Создать');
	$('#backbtn').hide();
	$('.folder-actions').hide();
	$('#folder_list a').removeClass('selected');
}

function saveImportFolder(e) {
	e.preventDefault();
	$('#savebtn').prop('disabled', true);
	$.ajax({
		url: contextPath + '/fold',
		type: 'POST',
		dataType: 'json',
		data: $('#folder_form').serialize()
	}).done(function(response) {
		resetFolderForm(true);
		loadImportFolders();
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
	var filledFields = getFilledFields();
	if (filledFields.length > 0) {
		if (!window.confirm("У Вас есть несохраненное расписание.\nПерейти к редактирование?"))
			return;
	}
	$('#folder_list a').removeClass('selected');
	$(this).addClass('selected');
	var form = $('#folder_form');
	var id = $(this).data('id');
	var folder = window.folders[id];
	form.find('input[name=id]').val(id);
	form.find('input[name=hour]').val(pad(folder.hour, 2));
	form.find('input[name=minute]').val(pad(folder.minute, 2));
	form.find('select[name=date]').val(folder.date);
	form.find('select[name=month]').val(folder.month);
	form.find('select[name=year]').val(folder.year);
	form.find('select[name=day]').val(folder.weekday);
	form.find('input[name=path]').val(folder.path);
	$('#savebtn').text('Сохранить');
	$('#backbtn').show();
	$('.folder-actions').show();
}

function deleteFolder(e) {
	e.preventDefault();
	if (!window.confirm('Вы действительно хотите удалить папку?'))
		return;
	
	$.ajax({
		url: contextPath + '/fold?action=delete',
		type: 'GET',
		data: {id: $('#folder_form').find('input[name=id]').val()},
		dataType: 'json'
	}).done(function(response) {
		resetFolderForm(true);
		loadImportFolders();
	});
}

function pad(num, size) {
	var s = num+"";
	while (s.length < size) s = "0" + s;
	return s;
}