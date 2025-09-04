package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.City;
import hibernate.Distric;
import hibernate.Province;
import hibernate.HibernateUtil;
import org.hibernate.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import org.hibernate.Criteria;

@WebServlet("/LoadLocationData")
public class LoadLocationData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();

        responseObject.addProperty("status", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria province = session.createCriteria(Province.class);
        List<Province> provinceList = province.list();

        Criteria distric = session.createCriteria(Distric.class);
        List<Distric> districList = distric.list();

        Criteria city = session.createCriteria(City.class);
        List<City> cityList = city.list();

        session.close();

        responseObject.add("provinceList", gson.toJsonTree(provinceList));
        responseObject.add("cityList", gson.toJsonTree(cityList));
        responseObject.add("districList", gson.toJsonTree(districList));

        responseObject.addProperty("status", true);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }
}
