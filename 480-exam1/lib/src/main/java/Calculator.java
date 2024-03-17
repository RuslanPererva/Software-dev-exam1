import java.util.Scanner;

public class Calculator {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner (System.in);
		int menu_choice, num1, num2;
		
		System.out.println("Menu:");
		System.out.println("1. Add");
		System.out.println("2. Subtract");
		System.out.println("3. Multiply");
		System.out.println("4. Divide");
		System.out.println("5. Remainder");
		System.out.println("Enter your choice (1-5): ");
		menu_choice=sc.nextInt();
		
		switch (menu_choice) {
		case 1:
			System.out.println("Enter a number: ");
			num1=sc.nextInt();
			System.out.println("Enter another number: ");
			num2=sc.nextInt();
			System.out.println(num1+num2);
			break;
		case 2:
			System.out.println("Enter a number: ");
			num1=sc.nextInt();
			System.out.println("Enter another number: ");
			num2=sc.nextInt();
			System.out.println(num1-num2);
			break;
		case 3:
			System.out.println("Enter a number: ");
			num1=sc.nextInt();
			System.out.println("Enter another number: ");
			num2=sc.nextInt();
			System.out.println(num1*num2);
			break;
		case 4:
			System.out.println("Enter a number: ");
			num1=sc.nextInt();
			System.out.println("Enter another number: ");
			num2=sc.nextInt();
			System.out.println(num1/num2);
			break;
		case 5:
			System.out.println("Enter a number: ");
			num1=sc.nextInt();
			System.out.println("Enter another number: ");
			num2=sc.nextInt();
			System.out.println(num1%num2);
			break;
		}
	}

}
