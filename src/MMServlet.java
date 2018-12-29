import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;









import java.util.List;

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
			System.out.println("hello");
			response.sendRedirect("addnewaccount.html");
			break;
			
		case "/addingAccount.mm":
			String accountHolderName = request.getParameter("account_hn");
			double accountBalance = Double.parseDouble(request.getParameter("account_bal"));
			boolean salary = request.getParameter("y").equalsIgnoreCase("y")?true:false;
			
			try {
				savingsAccountService.createNewAccount(accountHolderName, accountBalance, salary);
				PrintWriter writer = response.getWriter();
				writer.println("Account Opened successfully");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/closeAccount.mm":
			int accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			try {
				savingsAccountService.deleteAccount(accountNumber);
				PrintWriter writer = response.getWriter();
				writer.println("Account closed successfully");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/searchAccount.mm":
			int accountNumber2 = Integer.parseInt(request.getParameter("accountNumber"));
			SavingsAccount savingsAccount;
			try {
				savingsAccount = savingsAccountService.getAccountById(accountNumber2);
				String result = savingsAccount.getBankAccount().toString();
				
				 PrintWriter writer = response.getWriter();
				 String htmlResponse = "<html><h2>"+result+"</h2></html>";
				 writer.println(htmlResponse);
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
			
		case "/withDraw.mm":
			int accountNumber3 = Integer.parseInt(request.getParameter("accountNumber"));
			double amount = Double.parseDouble(request.getParameter("amount"));
			
			try {
				savingsAccount = savingsAccountService.getAccountById(accountNumber3);
				savingsAccountService.withdraw(savingsAccount, amount);
				DBUtil.commit();
				PrintWriter writer = response.getWriter();
				writer.println("Amount withdrawn successfully.Updated balance : rs"+savingsAccount.getBankAccount().getAccountBalance());
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
				savingsAccount = savingsAccountService.getAccountById(accountNumber4);
				savingsAccountService.deposit(savingsAccount, amount2);
			DBUtil.commit();
			PrintWriter writer = response.getWriter();
			writer.println("Amount Deposited successfully.Updated balance : rs"+savingsAccount.getBankAccount().getAccountBalance());
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
			
		case "/checkBalance.mm":
			int accountNumber5 = Integer.parseInt(request.getParameter("accountNumber"));
			
			try {
				savingsAccount = savingsAccountService.getAccountById(accountNumber5);
				double currentBalance = savingsAccount.getBankAccount().getAccountBalance();
				String currentBal = "<html><h2> current balance - Rs"+currentBalance+"</h2></html>";
				PrintWriter writer = response.getWriter();
				writer.println(currentBal);
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
			List<SavingsAccount> savingsAccounts;
			try {
				savingsAccounts = savingsAccountService.getAllSavingsAccount();
				PrintWriter out = response.getWriter();
				for (SavingsAccount savingAccount : savingsAccounts) {
				out.println(savingAccount);
				
				 
			}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
			
					
			
			
			
		}
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
