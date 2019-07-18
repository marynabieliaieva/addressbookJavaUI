package ru.stqa.pft.addressbook.tests;

import com.thoughtworks.xstream.XStream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GroupCreationTest extends TestBase {
  @DataProvider
  public Iterator<Object[]> validGroups() throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/groups.xml")));
    String xml = "";
    String line = reader.readLine();
    while (line != null){
      xml += line;
      line = reader.readLine();
    }
    XStream xStream = new XStream();
    xStream.processAnnotations(GroupData.class);
    List<GroupData> groups = (List<GroupData>) xStream.fromXML(xml);
    return groups.stream().map((g) -> new Object[] {g}).collect(Collectors.toList()).iterator();
  }

  @Test(dataProvider = "validGroups")
  public void testGroupCreation(GroupData group) throws InterruptedException {
    app.goTo().contactPage();
    Groups before = app.getGroupHelper().all(); //list of the elements
    //GroupData group = new GroupData().withName(name);
    app.getGroupHelper().createGroup(group);
    app.goTo().contactPage();
    assertThat(app.getGroupHelper().getGroupCount(), equalTo(before.size() + 1));
    Groups after = app.getGroupHelper().all(); //list of the elements
    assertThat(after.size(), equalTo(before.size() + 1));
    System.out.println("Was: " + before.size() + ", now: " + after.size());
    System.out.println("Was: " + before + ", now: " + after);
    assertThat(after, equalTo(
            before.withAdded(group.withId(after.stream().mapToInt((g) -> g.getId()).max().getAsInt())))); // Hamcrest assert
  }

}


