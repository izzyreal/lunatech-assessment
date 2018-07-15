<title>Query</title>
<%@page pageEncoding="UTF-8"%>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

<script>
	$(function() {

		$('#country').keypress(function() {
			$.ajax({
				url : "Auto",
				type : "post",
				data : '',
				success : function(data) {
					$("#country").autocomplete({
						source : data
					});

				},
				error : function(data, status, er) {
					console.log(data + "_" + status + "_" + er);
				},

			});

		});

	});
</script>


<div class="ui-widget">
	<form method="post" action="query">
		<label for="country">Country code or name: </label> <input id="country" name="country">
		<button type="submit" name="submitbutton">Submit</button>
	</form>
</div>