import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class waterFlow {
	int count1=0;
	Integer[][] min=new Integer[50][2];
	Integer[] swap=new Integer[50];
	
	public boolean check_if_exists(LinkedList<Node> explored,Node addData){
		Iterator<Node> iterator1=explored.iterator();
		while(iterator1.hasNext()){
			
			if(iterator1.next().getData().equals(addData.getData())){
				return true;
			}
		}
		return false;
	}
	
	
	public void compute_minarray(List<ArrayList<Integer>> offperiod,String node,int num_of_pipes,Vector<Vector<String>> edges,int path_cost){
		int i,j;
		count1=0;
		for(i=0;i<50;i++){
			for(j=0;j<2;j++){
				min[i][j]=0;
			}
		}
		for(i=0;i<num_of_pipes;i++){
			int temppathcost;
			if(edges.get(i).get(0).equals(node)){
				temppathcost=(path_cost+(Integer.parseInt(edges.get(i).get(2))));//%24;
				if(!(offperiod.get(i).contains(path_cost%24))){
					//to check if starting node has open edges
					min[count1][0]=temppathcost;
					System.out.println(temppathcost+"from this"+edges.get(i).get(1));
					min[count1][1]=i;
					count1++;
				}
			}	
		}
		for(i=0;i<count1;i++){
			for(j=0;j<count1-1;j++){
				if(min[j][0]>min[j+1][0]){
					swap=min[j];
					min[j]=min[j+1];
					min[j+1]=swap;
				}
				else if(min[j][0]==min[j+1][0]){
					int val=edges.get(min[j][1]).get(1).compareTo(edges.get(min[j+1][1]).get(1));
					if(val>0){
						swap=min[j];
						min[j]=min[j+1];
						min[j+1]=swap;
					}
				}
			}
		}
	}
	
	public waterFlow(){
		super();
	}

	public BufferedReader readFromFile(String readFrom){ //args[1]
		try {
			FileReader readInput= new FileReader(readFrom);
			BufferedReader reader= new BufferedReader(readInput);
			if(reader!=null)
			return reader;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime=System.currentTimeMillis();
		try {
			System.out.println(args[1]);
			waterFlow waterflow=new waterFlow();
			BufferedReader reader= new BufferedReader(waterflow.readFromFile(args[1]));
			String line=null;
			File file=new File("output.txt");
			if(file.createNewFile()){
				System.out.println("file is created");
			}
			else{
				System.out.println("the file exists");
			}
			PrintWriter output=new PrintWriter(file, "UTF-8");
			int count=0; //to check for number of test cases
			line=reader.readLine(); //read the number of test cases
			int no_of_test_cases= Integer.parseInt(line);	
			System.out.println("The count of test cases is ="+no_of_test_cases);
			while(count!= no_of_test_cases){
				if(!(line=reader.readLine()).isEmpty()){ //check if it is bfs dfs or ucs
					waterflow.interpret_file(line,reader,output);
					count++;
					System.out.println("one iteration done");
				}										
			}
			output.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime=System.currentTimeMillis();
		System.out.println("Your code took:"+(endTime-startTime));
	}
	
	public void dfs(PrintWriter output,int start_time,Vector<Vector<String>> edges,int num_of_pipes,String[] middle_states,String[] goal_states,String initial_node){
		System.out.println("in dfs");
		int path_cost=start_time;
		int flag=0;
		int child_count=0;
		Node addData[]=new Node[50];
		Node temp=new Node();
		Node temp1=new Node();
		ArrayList<Node> copystring=new ArrayList<Node>();
		int i=0;
		Stack<Node> stack=new Stack<Node>();
		ArrayList<Node> explored=new ArrayList<Node>();
		ArrayList<String> explored1=new ArrayList<String>();
		addData[0]=new Node(initial_node);//add initial node to stack
		addData[0].isInitial();
		stack.push(addData[0]);
		outerloop:
		while(true){
			if(stack.isEmpty()){
				flag=1;
				break;
			}
			temp=stack.pop();
			System.out.println("poop element:"+temp.getData());
			explored.add(temp);
			explored1.add(temp.getData());
			if(Arrays.asList(goal_states).contains(temp.getData())){
				System.out.println("breaking now"+temp.getData()+" "+flag);
				temp1=temp;
				while((temp1.getParent())!=null){
					path_cost+=1;
					temp1=temp1.getParent();
				}
				break outerloop;
			}
			for(i=0;i<num_of_pipes;i++){
				System.out.println("entered for "+i);
				if(temp.getData().equals(edges.get(i).get(0))){
					System.out.println("found a match");
					Node addData1=new Node(edges.get(i).get(1));
					addData1.setParent(temp);
					copystring.add(addData1);
					child_count++;
				}
			}
			Collections.sort(copystring,new dfs_sort());
			child_count--;
			while(child_count!=-1){
				/*if(!explored.contains(copystring.get(child_count)) || !(explored1.contains(copystring.get(child_count).getData()))){
				stack.push(copystring.get(child_count));
				}*/
				if(explored.contains(copystring.get(child_count)) || (explored1.contains(copystring.get(child_count).getData()))){
					
					}
				else{
					stack.push(copystring.get(child_count));
				}
				child_count--;
			}
			copystring.clear();
			child_count=0;
		}
		
		if(flag==0){
			System.out.println("goal reached with path_cost:"+(path_cost%24));
		output.println(temp.getData()+" "+(path_cost%24));
	}
else{
	System.out.println(flag);
	System.out.println("failed to reach any");
	output.println("None");
}
		
	}
	
	/*public void dfs(PrintWriter output,int start_time,Vector<Vector<String>> edges,int num_of_pipes,String[] middle_states,String[] goal_states,String initial_node){
		System.out.println("in dfs");
		int path_cost=start_time;
		int flag=0;
		int child_count=0;
		Node addData[]=new Node[50];
		Node temp=new Node();
		Node temp1=new Node();
		ArrayList<Node> copystring=new ArrayList<Node>();
		int i=0;
		Stack<Node> stack=new Stack<Node>();
		Stack<Node> explored=new Stack<Node>();
		addData[0]=new Node(initial_node);//add initial node to stack
		addData[0].isInitial();
		stack.push(addData[0]);
		outerloop:
			while(true){
				if(stack.isEmpty()){
					flag=1;
					break;
				}
				temp=stack.pop();
				System.out.println("poop element:"+temp.getData());
				explored.push(temp);
				if(Arrays.asList(goal_states).contains(temp.getData())){
					System.out.println("breaking now"+temp.getData()+" "+flag);
					temp1=temp;
					while((temp1.getParent())!=null){
						path_cost+=1;
						temp1=temp1.getParent();
					}
					break outerloop;
				}
				for(i=0;i<num_of_pipes;i++){
					System.out.println("entered for "+i);
					if((temp.getData()).equals(edges.get(i).get(0))){
						System.out.println("found a match");
						addData[i+1]=new Node(edges.get(i).get(1));
						addData[i+1].setParent(temp);
						System.out.println("added value:"+edges.get(i).get(1));
								System.out.println("queing");
									copystring.add(addData[i+1]);
									child_count++;
								
								
								//stack.push(addData[i+1]);
						}
					}
				System.out.println("child_count"+child_count);
				child_count--;
				while(child_count!=-1){
					int duplicate=0;
					Node copy1=copystring.remove(child_count);
				for(Node n:explored){
					String compare1=n.getData();
					if(compare1.equals(copy1.getData())){
						
						duplicate=1;
					}
					}
				if(duplicate==1){
					System.out.println("found duplicate, not adding");
					System.out.println(copy1.getData());
				}
				else{
					System.out.println("unique element");
					stack.push(copy1);
				//stack.push(copystring.remove(child_count));
					System.out.println("stack peek:"+stack.peek().getData());
					//System.out.println("child copystring: "+copystring.remove(child_count).getData());
					child_count--;
					duplicate=0;
				}
				}
				child_count=0;
				copystring.clear();
				}
				
		
				if(flag==0){
					System.out.println("goal reached with path_cost:"+(path_cost%24));
				output.println(temp.getData()+" "+(path_cost%24));
			}
		else{
			System.out.println(flag);
			System.out.println("failed to reach any");
			output.println("None");
		}
	
}*/
	
	public void bfs(PrintWriter output,int start_time,Vector<Vector<String>> edges,int num_of_pipes,String[] middle_states,String[] goal_states,String initial_node){
		System.out.println("in bfs");
		int path_cost=start_time;
		int flag=0;
		Node addData[]=new Node[50];
		Node temp=new Node();
		Node temp1=new Node();
		int i=0;
		Queue<Node> queue=new LinkedList<Node>();
		Queue<Node> explored=new LinkedList<Node>();
		addData[0]=new Node(initial_node); // add initial node to the queue
		addData[0].isInitial();
		queue.add(addData[0]);
		outerloop:
		while(true){
			if(queue.isEmpty()){
				flag=1;
				break;
			}
			//path_cost++;
			temp=queue.remove();
			explored.add(temp);
			for(i=0;i<num_of_pipes;i++){
				System.out.println("entered for "+i);
				if((temp.getData()).equals(edges.get(i).get(0))){
					System.out.println("found a match");
					addData[i+1]=new Node(edges.get(i).get(1));
					addData[i+1].setParent(temp);
					System.out.println("added value:"+edges.get(i).get(1));
					if(!queue.contains(addData[i+1]) && !explored.contains(addData[i+1])){
						System.out.println("entered dis");
						if(Arrays.asList(goal_states).contains(addData[i+1].getData())){
							System.out.println("breaking now"+addData[i+1].getData()+" "+flag);
							temp1=addData[i+1];
							while((temp1.getParent())!=null){
								path_cost+=1;
								temp1=temp1.getParent();
							}
							break outerloop;
						}
						else{
							System.out.println("queing");
							queue.add(addData[i+1]);
						}
					}
				}
			}
		}
		if(flag==0){
			System.out.println("goal reached with path_cost:"+(path_cost%24));
			output.println(addData[i+1].getData()+" "+(path_cost%24));
			}
		else{
			System.out.println(flag);
			System.out.println("failed to reach any");
			output.println("None");
		}
}
	public void ucs(PrintWriter output,int start_time,Vector<Vector<String>> edges,int num_of_pipes,String[] middle_states,String[] goal_states,String initial_node){
		System.out.println("in ucs");
		int path_cost=start_time;
		int flag=0;
		int i=0,j,k=0;
		List<ArrayList<Integer>> offperiod=new ArrayList<ArrayList<Integer>>();
		String[] copytemp=new String[2];
		int node_count=0;
		Node addData[]=new Node[50];
		Node temp=new Node();
		Node temp1=new Node();
		LinkedList<Node> queue=new LinkedList<Node>();
		LinkedList<Node> explored=new LinkedList<Node>();
		addData[node_count]=new Node(initial_node);
		//node_count++;
		addData[0].isInitial();
		for(i=0;i<num_of_pipes;i++){ // to split the off times
			int iterator=0;j=0;
			iterator=Integer.parseInt(edges.get(i).get(3));
			ArrayList<Integer> tocopy=new ArrayList<Integer>();
			offperiod.add(i,tocopy);
			System.out.println("number of off periods are: "+iterator);
			while(j!=iterator){
				copytemp=edges.get(i).get(4+j).split("-");
				for(k=Integer.parseInt(copytemp[0]);k<=Integer.parseInt(copytemp[1]);k++){
					offperiod.get(i).add(k);
				}
				System.out.println(offperiod.get(i));
				j++;
			}
		}
		int flagme=0;
		int final_cost=0;
		addData[0].setCost(start_time);
		queue.add(addData[0]);
		while(true){
			if(queue.isEmpty()){
				flag=1;
				break;
			}
			temp=queue.remove();
			path_cost=temp.getCost();
			compute_minarray(offperiod, temp.getData(), num_of_pipes, edges, path_cost);
			if(Arrays.asList(goal_states).contains(temp.getData())){
				System.out.println("reached goal");
				final_cost=temp.getCost();
				break;
			}
			explored.add(temp);
			for(i=0;i<num_of_pipes;i++){
				System.out.println("entered for "+i);
				if((temp.getData()).equals(edges.get(i).get(0))){
					System.out.println("found a match"+count1);
					for(j=0;j<count1;j++){
						addData[node_count]=new Node(edges.get(min[j][1]).get(1));
						addData[node_count].setParent(temp);
						addData[node_count].setCost(min[j][0]);
						System.out.println(min[j][0]+"is the cost");
						if(check_if_exists(explored,addData[node_count])){
						//queue.add(addData[node_count]);
						//System.out.println("added:"+addData[node_count].getData());
						//break;
						continue;
						}
						else if(!check_if_exists(queue,addData[node_count]))
						{
							queue.add(addData[node_count]);
							System.out.println("added:"+addData[node_count].getData());
							continue;
							//not there in queue
			
						}
						else{
							System.out.println("in aishwaryadesh");
							for(Node x: queue){
								System.out.println("in for");
								if(x.getData().equals(addData[node_count].getData())){
									System.out.println("in first if condition");
									if(x.getCost()>addData[node_count].getCost()){
										x.setCost(addData[node_count].getCost());
										
										//SSystem.out.println("updated value:"+x.getCost()+" "+addData[node_count].getCost());
										//queue.remove(x);
										//queue.addFirst(addData[node_count]);
										//queue.add(addData[node_count]);
										
									}
									
								}
							}
						}
						node_count++;
					}
					Collections.sort(queue, new ucs_comparator());
					break;
				}
			}
			//break; //for the time being
		}
		System.out.println("final_cost is:"+final_cost+"node count is:"+node_count);
		for(i=0;i<node_count;i++){
			System.out.println(addData[i].getData());
		}
		if(flag==0){
			System.out.println("goal reached with path_cost:"+(final_cost%24));
			output.println(temp.getData()+" "+(final_cost%24));
			}
		else{
			System.out.println(flag);
			System.out.println("failed to reach any");
			flag=0;
			output.println("None");
		}
}
	
	public void interpret_file(String algorithm, BufferedReader reader,PrintWriter output){
		String line;
		String[] goal_states=null;
		String[] middle_states= null;
		Vector<Vector<String>> edges=new Vector<Vector<String>>();
		Vector<String> temp=new Vector<String>();
		edges.setSize(10000); //no of elements in the vector
		int pointer=0;
		int num_of_pipes=0;
		int num_off_periods=0;
		int start_time=0;
		String initial_node=null;
		StringTokenizer st=null;
		try {
			if(!(line=reader.readLine()).isEmpty()){ //source node
				initial_node=line;
				}
			if(!(line=reader.readLine()).isEmpty()){ //adding goal states
				 st=new StringTokenizer(line);
				 goal_states=new String[st.countTokens()];
				while(st.hasMoreElements()){
					goal_states[pointer]=(java.lang.String) st.nextElement();
					System.out.print("The goal states are:"+goal_states[pointer]+" ");
					pointer++;
					}
				System.out.println("The number of goal states are: "+ goal_states.length);
			}
			//if(!(line=reader.readLine()).isEmpty()){ //adding intermediate states
			line=reader.readLine();
				pointer=0;
				st=new StringTokenizer(line);
				middle_states=new String[st.countTokens()];
				while(st.hasMoreElements()){
					middle_states[pointer]=(java.lang.String) st.nextElement();
					System.out.print("The intermediate states are:"+middle_states[pointer]+" ");
					pointer++;
				}
				System.out.println("The number of intermediate states are: "+middle_states.length);
			/*}
			else{
				
			}*/
			if(!(line=reader.readLine()).isEmpty()){ //number of pipes
				num_of_pipes=Integer.parseInt(line);
				System.out.println("The number of pipes are: "+num_of_pipes);
			}
			pointer=0;
			int iterator=0;
			while(pointer!=num_of_pipes){ //graph
				if(!(line=reader.readLine()).isEmpty()){
					String[] splitted=line.split(" ");
					num_off_periods=Integer.parseInt(splitted[3]);
					String first_node=splitted[0];
					String second_node=splitted[1];
					System.out.println(splitted[3]+ "is the number of off periods");
					edges.set(pointer, new Vector<String>());
					edges.get(pointer).setSize(1000);
					for(int i=0;i<num_off_periods+4;i++){
						edges.get(pointer).set(i, splitted[i]);
					}	
					temp.setSize(1000);
					if(pointer>0){
						iterator=pointer;
						System.out.println("iterator is:"+iterator);
						while(!(iterator<=0) && edges.get(iterator-1).get(0).equals(splitted[0])){	
							if(edges.get(iterator-1).get(1).compareTo(splitted[1])>0){
								Collections.copy(temp, edges.get(iterator-1));
								Collections.copy(edges.get(iterator-1), edges.get(iterator));
								System.out.println("new dest:"+edges.get(iterator-1).get(1));
								Collections.copy(edges.get(iterator), temp);
							}
							iterator--;
						}
					}
				}
				pointer++;
			}
			if(!(line=reader.readLine()).isEmpty()){ //storage of start time
				start_time=Integer.parseInt(line);
				System.out.println("The start time is :"+start_time);
			}
			
			switch(algorithm){
			case "BFS":
				System.out.println("bfs call made");
				bfs(output,start_time,edges,num_of_pipes,middle_states,goal_states,initial_node);
				break;
			case "DFS":
				dfs(output,start_time,edges,num_of_pipes,middle_states,goal_states,initial_node);
				break;
			case "UCS":
				ucs(output,start_time,edges,num_of_pipes,middle_states,goal_states,initial_node);
				break;
			default:
				break;
			}
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


class Node {
	private List<Node> children=new ArrayList<Node>();
	private Node parent=null;
	private String data=null;
	private Integer cost=null;
	public Node(){
		super();
	}
	public Node(String data){
		this.data=data;
	}
	
	public Node(String data,Node parent){
		this.data=data;
		this.parent=parent;
	}
	
	public void addChild(String data){
		Node child=new Node(data);
		this.children.add(child);
	}
	public void setCost(Integer cost){
		this.cost=cost;
	}
	
	public Integer getCost(){
		return this.cost;
	}
	
	public void setParent(Node parent){
		parent.addChild(this.data);
		this.parent=parent;
	}
	
	public Node getParent(){
		return this.parent;
	}
	public boolean isInitial(){
		return (this.parent==null);
	}
	
	public String getData(){
		return this.data;
	}
}

class ucs_comparator implements Comparator<Node>{

	@Override
	public int compare(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		if (arg0.getCost()==arg1.getCost()){
			return arg0.getData().compareTo(arg1.getData());
		}
		else if(arg0.getCost()<arg1.getCost()){
			return -1;
		}
		else{
			return 1;
		}
	}
	
}

class dfs_sort implements Comparator<Node>{

	@Override
	public int compare(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		return (arg0.getData().compareTo(arg1.getData()));
	}
	
}