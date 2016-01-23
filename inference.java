import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class inference {

	HashMap<String, ArrayList<String>> knowledge_base = new HashMap<String,ArrayList<String>>();
	static BufferedWriter output =null ;
	ArrayList<String> loop_list= new ArrayList<>();
	static int flag=0;
	public static void main(String[] args) 
	{
		try 
		{
			output=new BufferedWriter(new FileWriter("output.txt"));
			inference inference = new inference();
			ArrayList<String> query_list = new ArrayList<String>();
			BufferedReader input = new BufferedReader(new FileReader(args[1]));
			int no_of_queries = Integer.parseInt(input.readLine());
			for (int i = 0; i < no_of_queries; i++) 
			{
				query_list.add(input.readLine());
			}
			int no_of_clauses = Integer.parseInt(input.readLine());
			for (int i = 0; i < no_of_clauses; i++)
			{
				inference.add_to_kb(input.readLine());
			}
			for (int i = 0; i < no_of_queries; i++)
			{
				inference.loop_list= new ArrayList<String>();
				flag=0;
				inference.infer(query_list.get(i));
			}
			output.close();
		} 
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void add_to_kb(String clause) {
		String functions[] = clause.split(" => ");
		String predicate;
		String func_name[];
		if (functions.length == 1) {
			func_name = clause.split("\\(");
			predicate = func_name[0];

		} else {
			func_name = functions[1].split("\\(");
			predicate = func_name[0];
		}
		insert_in_kb(predicate, clause);
	}

	private void insert_in_kb(String predicate, String clause) {
		ArrayList<String> list_of_func=new ArrayList<String>();
		if (knowledge_base.containsKey(predicate)) {
			list_of_func = knowledge_base.get(predicate);
			list_of_func.add(clause);
			knowledge_base.put(predicate, list_of_func);
		} else {
			list_of_func = new ArrayList<String>();
			list_of_func.add(clause);
			knowledge_base.put(predicate, list_of_func);
		}
	}
	
	private void infer(String string) {
		try {
		ArrayList<String> goal_list = new ArrayList<String>();
		HashMap<String,String> temp=new HashMap<String,String>();
		goal_list.add(string);
		ArrayList<HashMap<String, String>> subs = backward_chain(goal_list,temp);
		if (subs.isEmpty()) {
			output.write("FALSE\r\n");
		}
		 else {
			output.write("TRUE\r\n");	
		 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<HashMap<String, String>> backward_chain(ArrayList<String> goal_list, HashMap<String, String> substitution) {
		ArrayList<HashMap<String, String>> answers_list = new ArrayList<HashMap<String,String>>();
		if (goal_list.isEmpty()) {
			answers_list.add(substitution);
			return answers_list;
		}
		String subgoal = goal_list.remove(0);
		
		subgoal = substitute(subgoal, substitution);
		if(loop_list.contains(subgoal)){
			return answers_list;
		}
		else if(in_goal_list(subgoal))
		{
			return answers_list;
		}
		String temp_subgoal=subgoal;
		String goal_name = get_function_name(subgoal);

		ArrayList<String> matching_clause = knowledge_base.get(goal_name);
		ArrayList<String> temp_matching_clause = knowledge_base.get(goal_name);
		subgoal = subgoal.substring(subgoal.indexOf("(") + 1, subgoal.indexOf(")"));

		if(matching_clause==null)
		{
			return answers_list;
		}
		
		String iterate;
		for (String str : matching_clause) {
			str=standardise_variables(str);
			String list_of_clauses[] = str.split(" => ");
			if (list_of_clauses.length == 1) {
				iterate = str;
			} else {
				iterate = list_of_clauses[1];
			}
			
			iterate = iterate.substring(iterate.indexOf("(") + 1, iterate.indexOf(")"));
			HashMap<String, String> new_substitution = unify_clause(iterate, subgoal, substitution);

			if (new_substitution != null) 
			{
				new_substitution = compose(substitution, new_substitution);
				ArrayList<String> antecedents = new ArrayList<String>();
				if (list_of_clauses.length != 1) {
					String lhs_clauses[] = list_of_clauses[0].split("\\^");

					for (int i = 0; i < lhs_clauses.length; i++) {
						lhs_clauses[i]=lhs_clauses[i].replaceAll("\\s", "");
						lhs_clauses[i]=substitute(lhs_clauses[i], new_substitution);
						antecedents.add(lhs_clauses[i]);
					}
				}
				
				if(loop_exists(antecedents))
				{
					continue;
				}
				
				if(is_recursive_loop(antecedents,temp_subgoal))
				{
					continue;
				}
				
				loop_list.add(temp_subgoal);
				ArrayList<HashMap<String, String>> final_answers = (backward_chain(antecedents, new_substitution));
				for (HashMap<String, String> theta : final_answers) {
					answers_list.add(theta);
				}
			}
		} 

		String loop_string = null;
		
		if(!loop_list.isEmpty()){
			 loop_string=loop_list.get(0);
		}
		
		loop_list= new ArrayList<String>();
		loop_list.add(loop_string);
		ArrayList<HashMap<String, String>> final_answer_list = new ArrayList<HashMap<String,String>>();
		for (HashMap<String, String> theta : answers_list) {
			ArrayList<String> copy_goal_list=new ArrayList<String>();
			copy_goal_list.addAll(goal_list);
			ArrayList<HashMap<String, String>> answers1 = backward_chain(copy_goal_list, theta);
			
			for (HashMap<String, String> theta1 : answers1)
				final_answer_list.add(theta1);
		}
		return final_answer_list;
	}

	private String substitute(String goal, Map<String, String> substitution) {
		String result = null;
		String clause_split_list[] = goal.split("\\(");
		String predicate = clause_split_list[0];
		String variables = clause_split_list[1];
		variables = variables.substring(0, variables.length() - 1);
		String variable_list[] = variables.split(",");
		String final_variables = "";
		for (String var : variable_list) {
			if (substitution.containsKey(var)) {
				final_variables = final_variables + substitution.get(var) + ",";
			} else{
				final_variables = final_variables + var + ",";
			}
		}
		final_variables = final_variables.substring(0, final_variables.length() - 1);
		result = predicate + "(" + final_variables + ")";
		return result;

	}
	
	private boolean in_goal_list(String copy_subgoal) {
		ArrayList<String> list= new ArrayList<String>();
		
		list.add(copy_subgoal);
		for(String s:loop_list)
		{
			if(is_recursive_loop(list, s))
			{
				return true;
			}
		}
		return false;
	}

	
	private String standardise_variables(String clause)
	{
		String list[]=clause.split(" => ");
		int flag1=0;
		int flag2=0;
		if(list.length==1)
		{
			return list[0];
		}
		else
		{
			String antec[]=list[0].split("\\^");
			String string1="";	
			for(int i=0;i<antec.length;i++)
			{
				antec[i]=antec[i].replaceAll("\\s", "");
				String substitute= antec[i].substring(antec[i].indexOf("(")+1, antec[i].indexOf(")"));
				String[] variable_list= substitute.split(",");
				String newstring="";
				
				for(int j=0;j<variable_list.length;j++)
				{
					if(is_variable(variable_list[j]))
					{
						System.out.println("is variable");
						variable_list[j]=variable_list[j]+flag+",";
						flag1=1;
						System.out.println("variable_list[j]="+variable_list[j]);
					}
					else{
						variable_list[j]=variable_list[j]+",";
					}
					System.out.println("variable_list[j]="+variable_list[j]);
					newstring=newstring+variable_list[j];
				}
				System.out.println("newstring"+newstring);
				if(newstring.charAt(newstring.length()-1)==','){
					newstring=newstring.substring(0, newstring.length()-1);
				}
				System.out.println("newstring "+newstring);
				string1=string1+get_function_name(antec[i])+"("+newstring+")"+" ^ ";
			}
			string1=string1.substring(0, string1.length()-3);
			string1=string1.concat(" => ");
			

			String consequent=list[1].substring(list[1].indexOf("(")+1,list[1].indexOf(")"));
			String args[]=consequent.split(",");
			String newstr="";
			for(int j=0;j<args.length;j++)
			{
				if(is_variable(args[j]))
				{
					args[j]=args[j]+flag+",";
					flag2=1;
				}
				else{
					args[j]=args[j]+",";
				}
				newstr=newstr+args[j];
			}
			
			if(newstr.charAt(newstr.length()-1)==',')
			newstr=newstr.substring(0,newstr.length()-1);
			string1=string1+get_function_name(list[1])+"("+newstr+")";
			System.out.println(string1+": is here");
			flag++;
			return string1;
		}
	}

	private HashMap<String, String> unify_clause(String value1, String value2, HashMap<String, String> substitutions) {

		if (substitutions == null)
			return null;
		else if (value1.equals(value2))
			return substitutions;
		else if (is_variable(value1)) {
			return unify_variables(value1, value2, substitutions);
		} else if (is_variable(value2)) {
			return unify_variables(value2, value1, substitutions);
		}

		else if (is_list_of_variables(value1) && is_list_of_variables(value2)) {
			int end1 = value1.indexOf(",");
			String firstArgTerm1 = value1.substring(0, end1);

			int end2 = value2.indexOf(",");
			String firstArgTerm2 = value2.substring(0, end2);

			String restterm1 = value1.substring(end1 + 1, value1.length());
			String restterm2 = value2.substring(end2 + 1, value2.length());

			return unify_clause(restterm1, restterm2, unify_clause(firstArgTerm1, firstArgTerm2, substitutions));

		} else
			return null;
	}
	
	private HashMap<String, String> compose(Map<String, String> substitution, Map<String, String> newSub) {

		HashMap<String, String> final_substitution= new HashMap<String,String>();
		
		for(Entry<String,String> entry:substitution.entrySet())
		{
			final_substitution.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, String> entry : newSub.entrySet()) {
			if (!final_substitution.containsKey(entry.getKey())) {
				final_substitution.put(entry.getKey(), entry.getValue());
			}
		}

		return final_substitution;
	}
	
	private boolean is_recursive_loop(List<String> antecedents, String consequents) {
		int j=0;
		
		String variable_list1=consequents.substring(consequents.indexOf("(")+1,consequents.indexOf(")"));
		String variables1[]=variable_list1.split(",");
		for(String s: antecedents)
		{
			if(get_function_name(s).equals(get_function_name(consequents)))
			{
				String variable_list=s.substring(consequents.indexOf("(")+1,s.indexOf(")"));
				String variables[]=variable_list.split(",");
				for( j=0;j<variables.length;j++)
				{
			
					if(is_variable(variables[j] )&& is_variable(variables1[j]))
					{
						int k=0;
						while(variables[j].charAt(k)==variables1[j].charAt(k) && Character.isAlphabetic(variables[j].charAt(k)))
						{
							k++;
						}
						if(k==0)
							break;
						else
							
						{
							int num1=Integer.parseInt(variables[j].substring(k,variables[j].length()));
							int num2=Integer.parseInt(variables1[j].substring(k,variables1[j].length()));
							if(num1!=num2)
							{
								continue;
							}
						}
					}
					else
					{
						if(variables[j].equals(variables1[j]))
						{
							continue;
						}
						else 
							break;
					}
				}
				if(j==variables.length)
				{
					
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean loop_exists(ArrayList<String> list) {
		Iterator<String >iterator=list.iterator();
		if(iterator.hasNext())
		{
			String string1=iterator.next();
			ArrayList<String> additional_list= new ArrayList<String>();
			while(all_are_variables(string1))
			{
				iterator.remove();
				additional_list.add(string1);
				if(iterator.hasNext())
					string1=iterator.next();
				else
				{
					for(String str:additional_list)
					{
						list.add(str);
					}
					int flag=0;
					for( String str : list)
					{
						System.out.println("in for");
						flag=0;
						ArrayList<String> rules=knowledge_base.get(get_function_name(str));
						if(rules.isEmpty())
							return true;
					}
					return false;
				}
			}
			for(String str:additional_list)
			{
				list.add(str);
			}
		}
		return false;
	}
	
	private boolean all_are_variables(String s) {
		
		String variable_list= s.substring(s.indexOf("(")+1,s.indexOf(")"));
		String[] variables= variable_list.split(",");
		for(int j=0;j<variables.length;j++)
		{
			if(!is_variable(variables[j]))
			{
				return false;
			}
		}
	return true;
	}

	private boolean is_list_of_variables(String clause) {
		if (clause.contains(",")) {
			return true;
		}
		return false;
	}

	private boolean is_variable(String clause) {
		if (!clause.contains(",") && Character.isLowerCase(clause.charAt(0)))
			return true;
		return false;
	}

	private HashMap<String, String> unify_variables(String clause1, String clause2, HashMap<String, String> substitution) {
		
		HashMap<String, String> final_substitution= new HashMap<String,String>();
		
		if (substitution.containsKey(clause1)) {
			return unify_clause(substitution.get(clause1), clause2, substitution);
		}
		else if (substitution.containsKey(clause2)) {
			return unify_clause(clause1, substitution.get(clause2), substitution);
		} 
		else {
			for(Entry<String,String>entry:substitution.entrySet())
			{
				final_substitution.put(entry.getKey(), entry.getValue());
			}
			final_substitution.put(clause1, clause2);
			return final_substitution;
		}
	}

	public String get_function_name(String clause) {
		String func[] = clause.split("\\(");
		return func[0];
	}

}