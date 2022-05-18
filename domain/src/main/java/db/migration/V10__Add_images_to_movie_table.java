package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V10__Add_images_to_movie_table extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {

        JdbcTemplate jdbcTemplate =
                new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));

        byte[] titanicImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2012-04-05_Titanic 3D.jpg").readAllBytes();
        byte[] masterImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-01-21_Master.jpg").readAllBytes();
        byte[] hotelImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-01-13_Hotel Transylvania.jpg").readAllBytes();
        byte[] homeTeamImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-01-28_Home Team.jpg").readAllBytes();
        byte[] deathNileImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-02-09_Death on the Nile.jpg").readAllBytes();
        byte[] iceImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-02-15_Against the Ice.jpg").readAllBytes();
        byte[] adamImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-03-11_The Adam Project.jpg").readAllBytes();
        byte[] batmanImage = this.getClass().getClassLoader()
                .getResourceAsStream("static/images/2022-03-14_The Batman.jpg").readAllBytes();

        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", batmanImage, 1);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", deathNileImage, 2);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", titanicImage, 3);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", iceImage, 4);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", hotelImage, 5);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", adamImage, 6);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", homeTeamImage, 7);
        jdbcTemplate.update("UPDATE movie SET image=? WHERE id=?", masterImage, 8);
    }
}
