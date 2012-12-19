package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.libs.Json.toJson;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonNode;

import com.joestelmach.natty.*;

import views.html.*;

import models.*;

public class Application extends Controller {
  static Form<Task> taskForm = form(Task.class);
  static Parser parser = new Parser();
  static DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

  public static Result index() {
    return redirect(routes.Application.tasks());
  }

  public static Result tasks() {
    return ok(
      views.html.index.render(Task.all(), taskForm)
    );
  }

  public static Result newTask() {
    Form<Task> filledForm = taskForm.bindFromRequest();
    if(filledForm.hasErrors()) {
      return badRequest(
        views.html.index.render(Task.all(), filledForm)
      );
    } else {
      Task.create(filledForm.get());
      return redirect(routes.Application.tasks());
    }
  }

  public static Result deleteTask(Long id) {
    Task.delete(id);
    return redirect(routes.Application.tasks());
  }

  public static Result readJson() {
    JsonNode json = request().body().asJson();
    if(json == null) {
      return badRequest("Expecting Json data");
    } else {
      String name = json.findPath("name").getTextValue();
      if(name == null) {
        return badRequest("Missing parameter [name]");
      } else {
        return ok("Hello " + name);
      }
    }
  }

  public static Result gimmeJson() {
    Map<String,String> d = new HashMap<String,String>();
    d.put("peter","foo");
    d.put("yay","value");
    return ok(toJson(d));
   }

   public static Result nattyExample() {
    List<DateGroup> groups = parser.parse("the day before next thursday");
    List<String> detected = new ArrayList<String>();

    for(DateGroup group:groups) {
      List<Date> dates = group.getDates();
      for(Date d:dates) {
        detected.add(iso8601DateFormat.format(d));
      }
    }

    return ok(toJson(detected));
   }

   public static Result parse() {
    JsonNode json = request().body().asJson();
    if(json == null) {
      return badRequest("Expecting Json data");
    } else {
      String text = json.findPath("text").getTextValue();
      if(text == null) {
        return badRequest("Missing parameter [text]");
      } else {
        List<DateGroup> groups = parser.parse(text);
        List<String> detected = new ArrayList<String>();

        for(DateGroup group:groups) {
          List<Date> dates = group.getDates();
          for(Date d:dates) {
            detected.add(iso8601DateFormat.format(d));
          }
        }

        return ok(toJson(detected));
      }
    }
   }

}