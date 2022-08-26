package io.github.melin.flink.jobserver.examples;

import io.github.melin.flink.jobserver.api.FlinkJob;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Arrays;

public class FlinkStreamDemo implements FlinkJob {

    @Override
    public void runJob(StreamExecutionEnvironment env,
                       StreamTableEnvironment tableEnv,
                       String[] args) throws Exception {

        final DataStream<Order> orderA = env.fromCollection(
                Arrays.asList(new Order(1L, "beer", 3),
                        new Order(1L, "diaper", 4),
                        new Order(3L, "rubber", 2)));

        final DataStream<Order> orderB = env.fromCollection(
                Arrays.asList(new Order(2L, "pen", 3),
                        new Order(2L, "rubber", 3),
                        new Order(4L, "beer", 1)));

        // convert the first DataStream to a Table object
        // it will be used "inline" and is not registered in a catalog
        final Table tableA = tableEnv.fromDataStream(orderA);

        // convert the second DataStream and register it as a view
        // it will be accessible under a name
        tableEnv.createTemporaryView("TableB", orderB);

        // union the two tables
        final Table result = tableEnv.sqlQuery("SELECT * FROM " + tableA
                + " WHERE amount > 2 UNION ALL SELECT * FROM TableB WHERE amount < 2");

        // convert the Table back to an insert-only DataStream of type `Order`
        tableEnv.toDataStream(result, Order.class).print();

        // after the table program is converted to a DataStream program,
        // we must use `env.execute()` to submit the job
        env.execute();
    }

    // *************************************************************************
    //     USER DATA TYPES
    // *************************************************************************

    /** Simple POJO. */
    public static class Order {
        public Long user;
        public String product;
        public int amount;

        // for POJO detection in DataStream API
        public Order() {}

        // for structured type detection in Table API
        public Order(Long user, String product, int amount) {
            this.user = user;
            this.product = product;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "Order{"
                    + "user="
                    + user
                    + ", product='"
                    + product
                    + '\''
                    + ", amount="
                    + amount
                    + '}';
        }
    }
}
