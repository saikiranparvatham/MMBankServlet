
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="addingAccount.mm">
<table>
<tr><td>Enter Account Holder Name</td>
<td><input type = "text" name=account_hn></td></tr>

<tr><td>
Enter initial amount:</td>
<td><input type="text" name="account_bal"></td></tr>
<tr><td>
Select whether salaried or not</td>
<td><input type = "radio" name="y" value="y">salaried
<input type = "radio" name="y" value="n">not salaried</td></tr>
<tr><td><input type="submit" name="submit" value="submit"></td></tr>
</table>
</form>
<div>
		<jsp:include page="homeLink.html"></jsp:include>
	</div>
</body></html>




