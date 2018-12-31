package com.moneymoney.account.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;









import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;


@WebServlet("*.mm")
public class MMServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher;
	static boolean counter = true;
    @Override
    public void init() throws ServletException {
    	try {
    		
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = 
					connection.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	super.init();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();
	
		switch(path)
		{
		case "/addnewaccount.mm":
			response.sendRedirect("addNewSAForm.jsp");
			break;
			
		case "/addingAccount.mm":
			String accountHolderName = request.getParameter("account_hn");
			double accountBalance = Double.parseDouble(request.getParameter("account_bal"));
			boolean salary = request.getParameter("y").equalsIgnoreCase("y")?true:false;
			
			try {
				savingsAccountService.createNewAccount(accountHolderName, accountBalance, salary);
				response.sendRedirect("getAllAccounts.mm");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/close.mm":
			response.sendRedirect("closeAccount.jsp");
			break;
			
		case "/closeAccount.mm":
			int accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			try {
				savingsAccountService.deleteAccount(accountNumber);
				response.sendRedirect("getAllAccounts.mm");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
			
		case "/search.mm":
			response.sendRedirect("Search.jsp");
			break;
			
		case "/searchAccount.mm":
			int accountNumber6 = Integer.parseInt(request.getParameter("txtAccountNumber"));
			try {
				SavingsAccount account = savingsAccountService.getAccountById(accountNumber6);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException | AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
			
		case "/withDraw.mm":
			int accountNumber3 = Integer.parseInt(request.getParameter("accountNumber"));
			double amount = Double.parseDouble(request.getParameter("amount"));
			
			try {
				SavingsAccount savingsAccount = savingsAccountService.getAccountById(accountNumber3);
				savingsAccountService.withdraw(savingsAccount, amount);
				DBUtil.commit();
				SavingsAccount account = savingsAccountService.getAccountById(accountNumber3);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		case "/deposit.mm":
			int accountNumber4 = Integer.parseInt(request.getParameter("accountNumber"));
			double amount2 = Double.parseDouble(request.getParameter("amount"));
			
			try {
				SavingsAccount account2 = savingsAccountService.getAccountById(accountNumber4);
				savingsAccountService.deposit(account2, amount2);
			DBUtil.commit();
			SavingsAccount account = savingsAccountService.getAccountById(accountNumber4);
			request.setAttribute("account", account);
			dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
			dispatcher.forward(request, response);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		case "/fundTransfer.mm":
			int senderAccountNumber = Integer.parseInt(request.getParameter("senderNumber"));
			int receiverAccountNumber = Integer.parseInt(request.getParameter("receiverNumber"));
			Double amount3 = Double.parseDouble(request.getParameter("amount"));
			
			SavingsAccount senderSavingsAccount;
			try {
				senderSavingsAccount = savingsAccountService.getAccountById(senderAccountNumber);
				SavingsAccount receiverSavingsAccount = savingsAccountService.getAccountById(receiverAccountNumber);
				savingsAccountService.fundTransfer(senderSavingsAccount, receiverSavingsAccount, amount3);
				PrintWriter writer = response.getWriter();
				writer.println("Fund Transaction successful");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
			
			
		case "/getAllAccounts.mm":
			try {
				List<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				request.setAttribute("accounts", accounts);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/sortByName.mm":
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				Set<SavingsAccount> accountSet;
				
				if(counter)
				{
				accountSet = new TreeSet<>(new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						return arg0.getBankAccount().getAccountHolderName().compareTo
								(arg1.getBankAccount().getAccountHolderName());
					}
				});
				counter = false;
				}
				else
				{
					accountSet = new TreeSet<>(new Comparator<SavingsAccount>() {
						@Override
						public int compare(SavingsAccount arg0, SavingsAccount arg1) {
							return arg0.getBankAccount().getAccountHolderName().compareTo
									(arg1.getBankAccount().getAccountHolderName());
						}
					});
					counter = true;
				}
				accountSet.addAll(accounts);
				
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/sortByBalance.mm":
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				Set<SavingsAccount> accountSet;
				if(counter)
				{
				accountSet = new TreeSet<>(new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						return Double.compare(arg0.getBankAccount().getAccountBalance(), arg1.getBankAccount().getAccountBalance());
					}
				});
				counter = false;
				}
				else{
					accountSet = new TreeSet<>(new Comparator<SavingsAccount>() {
						@Override
						public int compare(SavingsAccount arg0, SavingsAccount arg1) {
							return Double.compare(arg1.getBankAccount().getAccountBalance(), arg0.getBankAccount().getAccountBalance());
						}
					});
					counter = true;
				}
				accountSet.addAll(accounts);
				
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		
		case "/sortByAccountNumber.mm":
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				Set<SavingsAccount> accountSet;
				if(counter)
				{
				accountSet = new TreeSet<>(new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						return Integer.compare(arg0.getBankAccount().getAccountNumber(), arg1.getBankAccount().getAccountNumber());
					}
				});
				counter = false;
				}
				else
				{
					accountSet = new TreeSet<>(new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						return Double.compare(arg1.getBankAccount().getAccountBalance(), arg0.getBankAccount().getAccountBalance());
						}
					});
					counter = true;
				}
				accountSet.addAll(accounts);
				
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/updateForm.mm":
			response.sendRedirect("update.jsp");
			break;
			
		case "/updateAccount.mm":
			accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			
			SavingsAccount account;
			try {
				account = savingsAccountService.getAccountById(accountNumber);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("updateAccount.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/updateAccountImpl.mm":
			SavingsAccount savingsAccount;
			try {
				accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
				savingsAccount = savingsAccountService.getAccountById(accountNumber);
				String holderName = request.getParameter("account_hn");
				boolean salaryUpdate = request.getParameter("y").equalsIgnoreCase("y")?true:false;
			savingsAccount.getBankAccount().setAccountHolderName(holderName);
			savingsAccount.setSalary(salaryUpdate);
			savingsAccountService.updateAccount(savingsAccount);
			response.sendRedirect("getAllAccounts.mm");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
			
			
		default:
			break;
			
		/*case "/updateAccount.mm":
			int accountNumber6=Integer.parseInt(request.getParameter("accountNumber"));
			String name=request.getParameter("name");
			String salaried= request.getParameter("isSalary");
			if(name !=null && salaried!= null)
			{
				PrintWriter out = response.getWriter();
				out.println("<html><p>Enter name:<input type=\"text\",name=\"name\"<p><p>enter salaried or not:<input type=\"radio\" name=\"salaried\">salaried<input type=\"radio\" name=\"notsalaried\">Notsalaried</html>");
			}
			else if(name!=null)
			{
				response.sendRedirect("update.html");
			}
			break;*/
			
			
			
		}
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
