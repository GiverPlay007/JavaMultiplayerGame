package me.giverplay.modernal.server.tasks;

import java.io.IOException;
import java.net.Socket;

import me.giverplay.modernal.server.Server;
import me.giverplay.modernal.server.objects.ServerLogger;

public class SocketTask extends AbstractTask
{
	public SocketTask(String ID)
	{
		super(ID); 
	}

	@Override
	public void run()
	{
		Server server = Server.getServer();
		
		while(true)
		{
			try
			{
				Socket socket = server.getConnectionHandler().getConnection().getListener().accept();
				Server.getServer().getTaskManager().handleNewClientTask(socket);
				
				ServerLogger.log("Cliente novo conectado, aguardando autenticação");
			} 
			catch (IOException e)
			{
				ServerLogger.log("Falha ao estabelecer conexão com o cliente");
				e.printStackTrace();
			}
			
			try
			{
				Thread.sleep(1000 / 60);
			}
			catch(InterruptedException e)
			{
				ServerLogger.warn("Atividade interrompida");
			}
		}
	}
}
