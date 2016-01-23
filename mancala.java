
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class mancala {
	
	BufferedWriter traverse_log;
	static int player_num;
	static int cutoff_depth;
	
	public static void main(String args[])
	{
		BufferedReader input=null;
		BufferedWriter next_move=null;
		try 
		{
			input=new BufferedReader(new FileReader(args[1]));
		
			Pit pits[];
			int algorithm_type=Integer.parseInt(input.readLine());
			player_num=Integer.parseInt(input.readLine());
			cutoff_depth=Integer.parseInt(input.readLine());
			String row1=input.readLine();
			String pit_count[]=row1.split(" ");
			int num_of_pits=pit_count.length*2+2+1;
			pits=new Pit[num_of_pits];
			int playerPits=pit_count.length;
			
			int i,j;
			j=num_of_pits-1;
			int k=2;
			for(i=0;i<pit_count.length;j--,i++)
			{
				pits[j]=new Pit();
				pits[j].pit_count=Integer.parseInt(pit_count[i]);
				pits[j].name="A"+k;
				k++;
			}
			String row2=input.readLine();
			pit_count=row2.split(" ");
			
			for(i=0,j=2;i<pit_count.length;j++,i++)
			{
				System.out.println("iteration:"+i);
				pits[j]=new Pit();
				pits[j].pit_count=Integer.parseInt(pit_count[i]);
				pits[j].name="B"+j;
			}
			pits[1]=new Pit();
			pits[1].pit_count=Integer.parseInt(input.readLine());
			pits[1].name="A1";
			pits[playerPits+2]=new Pit();
			pits[playerPits+2].pit_count=Integer.parseInt(input.readLine());
			pits[playerPits+2].name="B"+(playerPits+2);
			
			State Board=new State(pits, num_of_pits-1, playerPits, player_num);
	
			mancala newgame=new mancala();
			switch(algorithm_type){
			case 1:
			{
				newgame.greedy_implementation(Board);
			}
			break;
			case 2:
			{
				Game_State s=new Game_State();
				s.board_state=Board;		
				s.parent=null;
				s.value=Integer.MIN_VALUE;
				s.name="root";
				s.depth=0;
				s.type="MAX";
				s.parent=null;
				newgame.traverse_log=new BufferedWriter(new FileWriter("traverse_log.txt"));
				try {
					
					newgame.traverse_log.write("Node,Depth,Value\r\n");
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				newgame.minmax_implementation(s);
			}
			break;
			case 3:
			{
				Game_State s=new Game_State();
				s.board_state=Board;		
				s.parent=null;
				s.value=Integer.MIN_VALUE;
				s.name="root";
				s.depth=0;
				s.type="MAX";
				s.parent=null;
				s.alpha_value=Integer.MIN_VALUE;
				s.beta_value=Integer.MAX_VALUE;
				newgame.traverse_log=new BufferedWriter(new FileWriter("traverse_log.txt"));
				try {
						newgame.traverse_log.write("Node,Depth,Value,Alpha,Beta\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				newgame.temp_List=new ArrayList<Game_State>();
				int value=newgame.alpha_beta_max(s,Integer.MIN_VALUE,Integer.MAX_VALUE);
				System.out.println(value);
				Game_State s1=newgame.last_move(s,value);
				System.out.println(s1);
				newgame.writetofile(s1.board_state);
			}
			break;
			}
		}	
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	
	
	public void greedy_implementation(State initial_board_state)
	{
		Game_State s=gensuccessors(initial_board_state);
		writetofile(s.board_state);
	}
			
	public Game_State gensuccessors(State b)
	{
		
		Game_State s = null;
		List<Game_State> successors=new ArrayList<Game_State>();
		int i,j;
		for(i=b.my_startpit_index();i<=b.otherMancala+b.playerPits;i++)
		{
			
			if(b.my_Pits[i].pit_count==0)
				continue;
			s=new Game_State();
			State successor= null;
			try 
			{
					successor = (State)b.clone();
					if(successor.gensuccessors1(i))
					{			
						s=gensuccessors(successor);
						
					}
					else
					{	
						int eval=successor.eval();
						s.board_state=successor;
						s.name=b.my_Pits[i].name;
						s.value=eval;	
					}
					successors.add(s);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(successors);
		return successors.get(0);
	}
	
	List<Game_State> temp_List;
	public void minmax_implementation(Game_State s)
	{
		temp_List=new ArrayList<Game_State>();
		int v=MaxValue(s);
		System.out.println(v);
		Game_State s1=last_move(s, v);
		writetofile(s1.board_state);
	
	}
	public boolean game_end(Game_State state)
	{
		if(state.board_state.othersideempty() && state.board_state.mysideempty())
			return true;
		return false;
	}
	
	public int MaxValue(Game_State state)
	{
		State b=state.board_state;
		Game_State s;
		int v=Integer.MIN_VALUE;
		if(player_num==1)
		{
			if(game_end(state))
			{
				
				
				if(state.depth!=cutoff_depth || (state.depth==cutoff_depth && state.type.equals(state.parent.type)))
				{
					try {
						
						traverse_log.write(state.name+","+state.depth+","+getValue(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				state.value=state.board_state.eval();
				try {
					traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(state.depth==1)
					temp_List.add(state);
				return state.value;
			}			
			state.value=v;
			try 
			{
				traverse_log.write(state.name+","+state.depth+","+state.display()+"\r\n");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			for(int i=b.my_startpit_index(); i<=b.otherMancala+b.playerPits ;i++)
			{
				
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State successor= null;
				try 
				{
						successor = (State)b.clone();		
						boolean flag=successor.gensuccessors1(i);
						s.board_state=successor;
						s.name=b.my_Pits[i].name;
			
						System.out.println("Child:"+s.name+successor);
						s.parent=state;
						if(state.type.equals("MAX"))
							s.depth=state.depth+1;
						else
							s.depth=state.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,MaxValue(s));
							state.value=v;	
							try 
							{
								traverse_log.write(state.name+","+state.depth+","+state.display()+"\r\n");
							} 
							catch (IOException e) 
							{
							// TODO Auto-generated catch block
								e.printStackTrace();
							}		
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
									s.value=s.board_state.eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									state.value=v;
									
									if(s.depth==1)
										temp_List.add(s);
									try {
										traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
							}
			
							else
							{
								v=Math.max(v,min_value(s));
								state.value=v;
								try 
								{
									traverse_log.write(state.name+","+state.depth+","+state.display()+"\r\n");
								} 
								catch (IOException e) 
								{
							
								e.printStackTrace();
								}
							}
						}	
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(state.depth==1)
				temp_List.add(state);
			return v;

		}
		else
		{
			if(game_end(state))
			{
				
				
				if(state.depth!=cutoff_depth || (state.depth==cutoff_depth && state.type.equals(state.parent.type)))
				{
					try {
						traverse_log.write(state.name+","+state.depth+","+getValue(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				state.value=state.board_state.eval();
				try {
					traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(state.depth==1)
					temp_List.add(state);
				return state.value;
			}

			
			state.value=v;
			try 
			{
			
				traverse_log.write(state.name+","+state.depth+","+state.display()+"\r\n");
			} 
			catch (IOException e) 
			{
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			for(int i=(state.board_state.size); i>state.board_state.otherMancala;i--)
			{
				
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();
						
						boolean flag=child.gensuccessors1(i);
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						//System.out.println("Child:"+s.name+child);
						s.parent=state;
						if(state.type.equals("MAX"))
							s.depth=state.depth+1;
						else
							s.depth=state.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,MaxValue(s));
							state.value=v;	
							try 
							{
			
								traverse_log.write(state.name+","+state.depth+","+state.display()+"\r\n");
							} 
							catch (IOException e) 
							{
							// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
									s.value=s.board_state.eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									state.value=v;
									if(s.depth==1)
										temp_List.add(s);
									
									try {
										traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
							}
							else
							{
								v=Math.max(v,min_value(s));
								state.value=v;
								try 
								{
			
									traverse_log.write(state.name+","+state.depth+","+state.display()+"\r\n");
								} 
								catch (IOException e) 
								{
							// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}	
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(state.depth==1)
				temp_List.add(state);
			return v;

		}
		
	}
	
	public int min_value(Game_State state)
	{
		State b=state.board_state;
		Game_State s;
		int v=Integer.MAX_VALUE;
		if(player_num==1)
		{
			if(game_end(state))
			{
				
				
				if(state.depth!=cutoff_depth || (state.depth==cutoff_depth && state.type.equals(state.parent.type)))
				{
					try {
						traverse_log.write(state.name+","+state.depth+","+getValue(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				state.value=state.board_state.eval();
				try {
					traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(state.depth==1)
					temp_List.add(state);
				return state.value;
			}

		
			//
			state.value=v;
			try {
				traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			for(int i=b.size;i>b.myMancala ;i--)
			{
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();
						boolean flag=child.gensuccessors2(i);
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						s.parent=state;
						if(state.type.equals("MIN"))
							s.depth=state.depth+1;
						else
							s.depth=state.depth;
						s.type="MAX";
						
			
						if(flag)
						{	
							v=Math.min(v,min_value(s));
							state.value=v;
							try {
								traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
								s.value=s.board_state.eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								
								state.value=v;
								if(s.depth==1)
									temp_List.add(s);
									try {
										traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
															
							}
							else
							{	
								v=Math.min(v,MaxValue(s));
								state.value=v;
								try {
									traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
								}
								catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}		
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(state.depth==1)
				temp_List.add(state);
			return v;
	
		}
		else
		{
			if(game_end(state))
			{
				if(state.depth!=cutoff_depth || (state.depth==cutoff_depth && state.type.equals(state.parent.type)))
				{
					try {
						traverse_log.write(state.name+","+state.depth+","+getValue(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				
				state.value=state.board_state.eval();
				try {
					traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(state.depth==1)
					temp_List.add(state);
				return state.value;
			}

		
	
			state.value=v;
			try {
				traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=b.otherstartpit_index();i<=b.myMancala+b.playerPits ;i++)
			{
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();
						boolean flag=child.gensuccessors2(i);
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						s.parent=state;
						if(state.type.equals("MIN"))
							s.depth=state.depth+1;
						else
							s.depth=state.depth;
						s.type="MAX";
						if(flag)
						{	
							
							v=Math.min(v,min_value(s));
							state.value=v;
							try {
								traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
								s.value=s.board_state.eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								 
								state.value=v;
								if(s.depth==1)
									temp_List.add(s);
									try {
										traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}								
							}
							else
							{
								v=Math.min(v,MaxValue(s));
								state.value=v;
								try {
									traverse_log.write(state.name+","+state.depth+","+getValue(state.value)+"\r\n");
								}
								catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}		
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(state.depth==1)
				temp_List.add(state);
			return v;
	
		}
	}
	
	
	public void writetofile(State b)
	{
		BufferedWriter out=null;
		try 
		{
			out=new BufferedWriter(new FileWriter("next_state.txt"));
			Pit[] Pits=b.my_Pits;
			for(int i=b.size;i>b.size-b.playerPits;i--)
				out.write(Pits[i].pit_count+" ");
			out.write("\r\n");
			for(int i=2;i<=b.playerPits+1;i++)
				out.write(Pits[i].pit_count+" ");
			out.write("\r\n"+Pits[1].pit_count);
			int x=b.playerPits+2;
			out.write("\r\n"+Pits[x].pit_count+"\r\n");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
		try{
				if(out!=null)
				{
					out.close();
				}
				if(traverse_log!=null)
				{
					traverse_log.close();
				}
			}
			catch(IOException e)
			{
		 		e.printStackTrace();
			}
		}
	}	
	

	public Game_State end_pos(Game_State s,int value)
	{
		Game_State f=null;
		List<Game_State> successors=gensuccessor(s);
		if(successors!=null)
		{
			if(successors.get(0).type.equals("MIN"))
			{
				for(Game_State state: successors)
				{
					if(state.value==value)
					{
						f=last_move(state, value);
						return f;
					}
				}
			}
		}
		else
		if((s.value)==value)
		{
			return s;
		}
		return s;
	}
	
	
	class sort_order implements Comparator<Game_State>
	{
		@Override
		public int compare(Game_State arg1, Game_State arg2) {
			// TODO Auto-generated method stub
			return (arg1.name).compareTo(arg2.name);
		}
	}
	
	public Game_State last_move(Game_State state1,int value)
	{
		Game_State temp=null;
		List<Game_State> successors=gensuccessor(state1);
		if(successors!=null)
		{
			if(successors.get(0).type.equals("MIN"))
			{
				for(Game_State state: successors)
				{
					if(state.value==value)
					{
			
						temp=last_move(state, value);
						return temp;
					}
				}
				
			}
		}
		else
		if((state1.value)==value)
		{
			System.out.println("making final_move");
			return state1;
		}
		return state1;
	}
	

	public List<Game_State> gensuccessor(Game_State s)
	{
		List<Game_State> successors=new ArrayList<Game_State>();
		boolean flag=false;
		
		for(Game_State child:temp_List)
		{
			if(child.parent!=null && child.parent==s)
			{
				successors.add(child);
				flag=true;
			}
		}
		if(flag==true)
		{
			Collections.sort(successors, new sort_order());
			return successors;
		}
		return null;
	}
	
	public boolean isCutoff_depth(int depth)
	{
		if(depth==cutoff_depth)
		{
			return true;
		}
		return false;
	}
	public int alpha_beta_max(Game_State s1,int alpha,int beta)
	{
		State b=s1.board_state;
		Game_State s;
		int v=Integer.MIN_VALUE;
		if(player_num==1)
		{
			if(game_end(s1))
			{
				if(s1.depth!=cutoff_depth ||   (s1.depth==cutoff_depth && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+getValue(v)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.board_state.eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_List.add(s1);
				return s1.value;
			}

			
			s1.value=v;
			try 
			{
				//System.out.println("MAX: "+s.name+","+s.depth+","+s.print()+"\r\n");
				traverse_log.write(s1.name+","+s1.depth+","+s1.display()+","+getValue(alpha)+","+getValue(beta)+"\r\n");
			} 
			catch (IOException e) 
			{
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i=b.my_startpit_index(); i<=b.otherMancala+b.playerPits ;i++)
			{
				
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();		
						boolean flag=child.gensuccessors1(i);
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						System.out.println("Child:"+s.name+child);
						s.parent=s1;
						if(s1.type.equals("MAX"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,alpha_beta_max(s,alpha,beta));
							s1.value=v;	
								
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
									s.value=s.board_state.eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									s1.value=v;
									if(s.depth==1)
										temp_List.add(s);
									
									
							}
							else
							{
								v=Math.max(v,alpha_beta_min(s,alpha,beta));
								s1.value=v;
							
							}
						}
						
						if(v>=beta)
						{
							
							try 
							{
							//System.out.println("MAX: "+s.name+","+s.depth+","+s.print()+"\r\n");
								traverse_log.write(s1.name+","+s1.depth+","+s1.display()+","+getValue(alpha)+","+getValue(beta)+"\r\n");
							} 
							catch (IOException e) 
							{
						// TODO Auto-generated catch block
							e.printStackTrace();
							}
	
							
							return v;
						}
						alpha=Math.max(alpha,v);
						
						
						try 
						{
			
							traverse_log.write(s1.name+","+s1.depth+","+s1.display()+","+getValue(alpha)+","+getValue(beta)+"\r\n");
						} 
						catch (IOException e) 
						{
					// TODO Auto-generated catch block
						e.printStackTrace();
						}
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_List.add(s1);
			return v;

		}
		else
		{
			if(game_end(s1))
			{
				if(s1.depth!=cutoff_depth || (s1.depth==cutoff_depth && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+getValue(v)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.board_state.eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_List.add(s1);
				return s1.value;
			}

			
			s1.value=v;
			try 
			{
				traverse_log.write(s1.name+","+s1.depth+","+s1.display()+","+getValue(alpha)+","+getValue(beta)+"\r\n");
			} 
			catch (IOException e) 
			{
		
				e.printStackTrace();
			}
			
			
			for(int i=(s1.board_state.size); i>s1.board_state.otherMancala;i--)
			{
				
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();
						
						boolean flag=child.gensuccessors1(i);
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						System.out.println("Child:"+s.name+child);
						s.parent=s1;
						if(s1.type.equals("MAX"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,alpha_beta_max(s,alpha,beta));
							s1.value=v;	
						
						
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
									s.value=s.board_state.eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									s1.value=v;
									if(s.depth==1)
										temp_List.add(s);
									
							}
							else
							{
								v=Math.max(v,alpha_beta_min(s,alpha,beta));
								s1.value=v;
							}
							
							
						}	
						if(v>=beta)
						{
							
							try 
							{
						
								traverse_log.write(s1.name+","+s1.depth+","+s1.display()+","+getValue(alpha)+","+getValue(beta)+"\r\n");
							} 
							catch (IOException e) 
							{
						// TODO Auto-generated catch block
							e.printStackTrace();
							}
							
							return v;
						}
						alpha=Math.max(alpha,v);
						
						try 
						{
						
							traverse_log.write(s1.name+","+s1.depth+","+s1.display()+","+getValue(alpha)+","+getValue(beta)+"\r\n");
						} 
						catch (IOException e) 
						{
					// TODO Auto-generated catch block
						e.printStackTrace();
						}
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_List.add(s1);
			return v;

		}
		
	}
	
	
	
	public int alpha_beta_min(Game_State s1,int alpha,int beta)
	{
		State b=s1.board_state;
		Game_State s;
		int v=Integer.MAX_VALUE;
		if(player_num==1)
		{
			if(game_end(s1))
			{
				if(s1.depth!=cutoff_depth || (s1.depth==cutoff_depth && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+getValue(v)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			
				s1.value=s1.board_state.eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_List.add(s1);
				return s1.value;
			}

		
			
			s1.value=v;
			try {
				traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			for(int i=b.size;i>b.myMancala ;i--)
			{
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();
						boolean flag=child.gensuccessors2(i);
					
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						s.parent=s1;
						if(s1.type.equals("MIN"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MAX";
						
			
						if(flag)
						{	
							v=Math.min(v,alpha_beta_min(s,alpha,beta));
							s1.value=v;
							
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
								s.value=s.board_state.eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								
								s1.value=v;
								if(s.depth==1)
									temp_List.add(s);						
							}
							else
							{	
								v=Math.min(v,alpha_beta_max(s,alpha,beta));
								s1.value=v;
							}	
						}	
						
						if(v<=alpha)
						{
							try {
								traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
							}
							catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							return v;
						}
						
						beta=Math.min(beta, v);
						try {
							traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
						}
						catch (IOException e) {
						
						e.printStackTrace();
						}
						
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_List.add(s1);
			return v;
	
		}
		else
		{
			if(game_end(s1))
			{
				if(s1.depth!=cutoff_depth || (s1.depth==cutoff_depth && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+getValue(v)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.board_state.eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_List.add(s1);
				return s1.value;
			}		
			s1.value=v;
			try {
				traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=b.otherstartpit_index();i<=b.myMancala+b.playerPits ;i++)
			{
				if(b.my_Pits[i].pit_count==0)
				{
					continue;
				}
				s=new Game_State();
				State child= null;
				try 
				{
						child = (State)b.clone();
						boolean flag=child.gensuccessors2(i);
						s.board_state=child;
						s.name=b.my_Pits[i].name;
						s.parent=s1;
						if(s1.type.equals("MIN"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MAX";
						
						if(flag)
						{	
							
							v=Math.min(v,alpha_beta_min(s,alpha,beta));
							s1.value=v;
						}
						else
						{
							if(s.depth==cutoff_depth)
							{
								s.value=s.board_state.eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+getValue(s.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								 
								s1.value=v;
								if(s.depth==1)
									temp_List.add(s);
																
							}
							else
							{	
								v=Math.min(v,alpha_beta_max(s,alpha,beta));
								s1.value=v;
								
							}
						}	
						
						if(v<=alpha)
						{
							try {
								traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
							}
							catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							return v;
						}
						
						beta=Math.min(beta, v);
						try {
							traverse_log.write(s1.name+","+s1.depth+","+getValue(s1.value)+","+getValue(alpha)+","+getValue(beta)+"\r\n");
						}
						catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
						
						
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_List.add(s1);
			return v;
	
		}
	}
	
	
	public String getValue(int val)
	{
		if(val==Integer.MAX_VALUE)
		{
			return "Infinity";
		}
		if(val==Integer.MIN_VALUE)
			return "-Infinity";
		return val+"";	
	}
}

class State implements Cloneable
{
	int myplayer;
	Pit my_Pits[];
	int myMancala;
	int otherMancala;
	int size;
	int playerPits;
	
	public State(State board)
	{
		this.otherMancala=board.otherMancala;
		this.myMancala=board.myMancala;
		this.my_Pits=board.my_Pits.clone();
		this.size=board.size;
		this.playerPits=board.playerPits;
		this.myplayer=board.myplayer;
	}
	
	public boolean mysideempty()
	{
		if(myplayer==1)
		{
			for(int i=2;i<=(playerPits+1);i++)
			{
				if(my_Pits[i].pit_count!=0)
					return false;
			}
			return true;
		}
		else
		if(myplayer==2)
		{
			for(int i=(playerPits+3);i<=size;i++)
			{
				if(my_Pits[i].pit_count!=0)
					return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean othersideempty()
	{
		if(myplayer==1)
		{
			for(int i=(playerPits+3);i<=size;i++)
			{
				if(my_Pits[i].pit_count!=0)
					return false;
			}
			return true;
		}
		else
		if(myplayer==2)
		{
			for(int i=2;i<=(playerPits+1);i++)
			{ 
				if(my_Pits[i].pit_count!=0)
					return false;
			}
		}
		return true;
	}
	public boolean has_game_ended()
	{
		boolean flag=false;
		if(myplayer==1)
		{
			if(mysideempty())
			{
				flag=true;
				for(int i=(playerPits+3);i<=size;i++)
				{
					if(my_Pits[i].pit_count!=0)
					{
						my_Pits[otherMancala].pit_count+=my_Pits[i].pit_count;
						my_Pits[i].pit_count=0;
					}
				}
			}
			else
			if(othersideempty())
			{
				flag=true;
				for(int i=2;i<=(playerPits+1);i++)
				{
					if(my_Pits[i].pit_count!=0)
					{
						my_Pits[myMancala].pit_count+=my_Pits[i].pit_count;
						my_Pits[i].pit_count=0;
					}
				}

			}
			return flag;

		}
		else
		if(myplayer==2)
		{
			if(mysideempty())
			{
				flag=true;
				for(int i=2;i<=playerPits+1;i++)
				{
					if(my_Pits[i].pit_count!=0)
					{
						my_Pits[otherMancala].pit_count+=my_Pits[i].pit_count;
						my_Pits[i].pit_count=0;
					}
				}
			}
			else
			if(othersideempty())
			{
				flag=true;
				for(int i=playerPits+3;i<=size;i++)
				{
					if(my_Pits[i].pit_count!=0)
					{
						my_Pits[myMancala].pit_count+=my_Pits[i].pit_count;
						my_Pits[i].pit_count=0;
					}
				}
			}
			return flag;
		}
		return flag;
	}
	
	
	public State( Pit[] Pits, int size,int playerPits,int player) {
		super();
		myplayer=player;
		if(player==1)
		{
			myMancala = (playerPits+2);
			otherMancala = 1;
		}
		else
		if(player==2)
		{
			otherMancala = (playerPits+2);
			myMancala = 1;
		}
		this.my_Pits = Pits;
		this.size = size;
		this.playerPits = playerPits;
	}
	

	protected Object clone() throws CloneNotSupportedException {
		
		State b1 = null;
	    b1 = (State) super.clone();
	    b1.my_Pits=new Pit[size+1];
	    for(int i=1;i<=size;i++)
	    {
	    	b1.my_Pits[i]=(Pit) my_Pits[i].clone();
	    }
	    this.my_Pits.clone();
	    return b1;
	}
	
	public boolean is_my_mancala(int num)
	{
		if(myMancala==num)
			return true;
		return false;
	}
	
	public boolean other_mancala(int num)
	{
		if(otherMancala==num)
			return true;
		return false;
	}
	
	
	public int my_startpit_index()
	{
		if(myplayer==1)
			return 2; 
		else
			return (otherMancala+1);
	}
	public int eval()
	{
			return my_Pits[myMancala].pit_count-my_Pits[otherMancala].pit_count;
	}
	
	public boolean playerpit(int pitNumber)
	{
		if(myplayer==1)
		{
			if(pitNumber>=2 && pitNumber<=playerPits+1)
				return true;
		}
		else
		if(myplayer==2)
		{
			if(pitNumber>=playerPits+3 && pitNumber<=size)
				return true;
		}	
		return false;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(my_Pits);
	}
	
	public boolean isEmpty(int pitNumber)
	{
		if(my_Pits[pitNumber].pit_count==1)
		{
			return true;
		}
		return false;
	}
	
	
	public boolean gensuccessors1(int pit)
	{
		
		int count1=my_Pits[pit].pit_count;
		int pitNum=pit;
		my_Pits[pit].pit_count=0;
		while(count1!=0)
		{
			if((pitNum+1)>size)
			{
				pitNum=0;
			}
			if(other_mancala(pitNum+1))
				++pitNum;
			my_Pits[++pitNum].pit_count+=1;
			count1--;
		}
		
		if(is_my_mancala(pitNum))
		{
			has_game_ended();
			return true;
		}
		else
			if(has_game_ended())
			{
				return false;
			}
			else
		if(playerpit(pitNum) && isEmpty(pitNum))
		{
		
			my_game_end(pitNum);
			if(has_game_ended())
			{
				return false;
			}
			return false;
		}
		if(has_game_ended())
		{
			return false;
		}
		return false;
	}
	
	public int otherstartpit_index() {
		// TODO Auto-generated method stub
		if(myplayer==1)
			return (myMancala+1); 
		else
			return 2;
		
	}
	
	public boolean gensuccessors2(int pit) {
	
		int stones=my_Pits[pit].pit_count;
		int pitNum=pit;
		my_Pits[pit].pit_count=0;
		while(stones!=0)
		{
			if((pitNum+1)>size)
			{
				pitNum=0;
			}
			
			if(is_my_mancala(pitNum+1))
				++pitNum;
			my_Pits[++pitNum].pit_count+=1;
			stones--;
		}
		
		if(other_mancala(pitNum))
		{
			has_game_ended();
			return true;
		}
			else
			if(has_game_ended())
			{
				return false;
			}
			else
		if(!playerpit(pitNum) && !is_my_mancala(pitNum) && isEmpty(pitNum))
		{
			other_end_game(pitNum);
			if(has_game_ended())
			{
				return false;
			}
			return false;
		}
		if(has_game_ended())
		{
			return false;
		}
		return false;
	}
	
	private void other_end_game(int pitNum) {
		String myPit=my_Pits[pitNum].name;
		String s=new String();
		if(myPit.charAt(0)=='A')
		{
			s=myPit.replace('A', 'B');
		}
		else
			s=myPit.replace('B', 'A');
		
		for(Pit p: my_Pits)
		{
		
			if(p!=null)
			{
		
				if(p.name.equals(s))
				{
		
					my_Pits[otherMancala].pit_count+=my_Pits[pitNum].pit_count;
					my_Pits[otherMancala].pit_count+=p.pit_count;
					p.pit_count=0;
					my_Pits[pitNum].pit_count=0;
				}
			}
		}
		
	}
	public void my_game_end(int pitNum)
	{
		String myPit=my_Pits[pitNum].name;
		String s=new String();
		if(myPit.charAt(0)=='A')
		{
			s=myPit.replace('A', 'B');
		}
		else
			s=myPit.replace('B', 'A');
	
		for(Pit p: my_Pits)
		{
			if(p!=null)
			{
	
				if(p.name.equals(s))
				{
	
					my_Pits[myMancala].pit_count+=my_Pits[pitNum].pit_count;
					my_Pits[myMancala].pit_count+=p.pit_count;
					p.pit_count=0;
					my_Pits[pitNum].pit_count=0;
					has_game_ended();
					return;
				}
			}
		}	
	}
}


class  Pit implements Cloneable
{
	int pit;
	 String name;
	 int pit_count;
	
	
	public String toString() {
		return name+"-"+pit_count+" ";
	}
	
	
	
	public Pit() {
		super();
		
	}
	public Pit(String name, int stones) {
		super();
		this.name = name;
		this.pit_count = stones;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		Pit p=(Pit) super.clone();
		p.name=name;
		p.pit_count=pit_count;
		return p;
		
	}
}

class Game_State implements Comparable<Game_State>
{
	String name;
	int value;
	int alpha_value;
	int beta_value;
	State board_state;
	Game_State parent;
	String type;
	int depth;
	
	@Override
	public int compareTo(Game_State s) {
		// TODO Auto-generated method stub
		if(this.value==s.value)
		{
			return name.compareTo(s.name);
		}
		else
			return s.value-value;
	}

	public String display()
	{
		if(value==Integer.MAX_VALUE)
		{
			return "Infinity";
		}
		if(value==Integer.MIN_VALUE)
			return "-Infinity";
		return value+"";
	}
	@Override
	public String toString() {
		return "Game_State: [name=" + name + ", v=" + value + ", "+" b=" + board_state + ", "+ ", type=" + type + ", depth=" + depth + "]";
	}
	
}