import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

class Inventario {
    private AtomicInteger stock;
    private AtomicInteger entregasRealizadas;
    private AtomicInteger ventasRealizadas;

    public Inventario(int inventarioInicial) {
        this.stock = new AtomicInteger(inventarioInicial);
        this.entregasRealizadas = new AtomicInteger(0);
        this.ventasRealizadas = new AtomicInteger(0);
    }

    // Método para realizar una entrega (incrementar inventario)
    public void realizarEntrega() {
        stock.incrementAndGet();
        entregasRealizadas.incrementAndGet();
    }

    // Método para intentar una venta (decrementar inventario si hay stock)
    public void intentarVenta() {
        int actual;
        int nuevo;
        do {
            actual = stock.get();
            if (actual <= 0) {
                return; // No hay stock, no se puede vender
            }
            nuevo = actual - 1;
        } while (!stock.compareAndSet(actual, nuevo));

        ventasRealizadas.incrementAndGet();
    }

    public int getStock() {
        return stock.get();
    }

    public int getEntregasRealizadas() {
        return entregasRealizadas.get();
    }

    public int getVentasRealizadas() {
        return ventasRealizadas.get();
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        
        int inventarioInicial = random.nextInt(101); // 0-100
        int numProductores = random.nextInt(5) + 1;  // 1-5
        int numConsumidores = random.nextInt(5) + 1; // 1-5
        int operacionesPorHilo = random.nextInt(191) + 10; // 10-200

        Inventario inventario = new Inventario(inventarioInicial);

        // Crear hilos productores (entregas)
        Thread[] productores = new Thread[numProductores];
        Runnable tareaProductor = () -> {
            for (int i = 0; i < operacionesPorHilo; i++) {
                inventario.realizarEntrega();
            }
        };

        for (int i = 0; i < numProductores; i++) {
            productores[i] = new Thread(tareaProductor);
        }

        // Crear hilos consumidores (ventas)
        Thread[] consumidores = new Thread[numConsumidores];
        Runnable tareaConsumidor = () -> {
            for (int i = 0; i < operacionesPorHilo; i++) {
                inventario.intentarVenta();
            }
        };

        for (int i = 0; i < numConsumidores; i++) {
            consumidores[i] = new Thread(tareaConsumidor);
        }

        // Iniciar todos los hilos
        for (Thread productor : productores) {
            productor.start();
        }
        for (Thread consumidor : consumidores) {
            consumidor.start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread productor : productores) {
            productor.join();
        }
        for (Thread consumidor : consumidores) {
            consumidor.join();
        }

        // Calcular resultados
        int totalEntregas = inventario.getEntregasRealizadas();
        int totalVentas = inventario.getVentasRealizadas();
        int inventarioFinal = inventario.getStock();

        // Imprimir resultado
        System.out.println("Inventario inicial: " + inventarioInicial);
        System.out.println("Total de entregas realizadas: " + totalEntregas);
        System.out.println("Total de ventas realizadas: " + totalVentas);
        System.out.println("Inventario final: " + inventarioFinal);
        System.out.println("Comprobación: " + inventarioInicial + " + " + totalEntregas + " - " + totalVentas + " = " + (inventarioInicial + totalEntregas - totalVentas));
    }
}