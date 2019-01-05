package org.ys.activiti;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestActiviti {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    private String processKey = "qjlc2";
    @Test
    public void testInit(){
        try {
            String filePath = "D:\\idea-workspace\\activiti\\mainActiviti\\src\\main\\resources\\processes\\processes.zip";
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
            Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
            System.out.println("--------------------"+deploy.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInit2(){
        try {
           repositoryService.createDeployment().name("请假流程2").addClasspathResource("processes/qjlc2.bpmn")
                    .addClasspathResource("processes/qjlc2.png").deploy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testDelete(){
    	repositoryService.deleteDeployment("27501", true);
    }
    
    @Test
    public void testStartProcess() throws Exception {
    	Map<String,Object> variables = new HashMap<String,Object>();
    	variables.put("applyUserId", "张三");
		runtimeService.startProcessInstanceByKey(processKey, variables );
    }
    
    @Test
    public void testCompleteTask() throws Exception {
    	TaskQuery taskQuery = taskService.createTaskQuery();
    	taskQuery.taskAssignee("张三");
    	List<Task> taskList = taskQuery.list();
    	if(null != taskList && taskList.size() > 0) {
    		Task task = taskList.get(0);
    		taskService.complete(task.getId());
    	}
    }
    
    @Test
    public void testCompleteTask2() throws Exception {
    	taskService.complete("87504");
    }    
    
    @Test
    public void testCompleteTask3() throws Exception {
    	Map<String,Object> variables = new HashMap<String,Object>();
    	variables.put("outcome", "同意");
    	String taskId = "92504";
    	taskService.complete(taskId,variables);
    }  
    
    @Test
    public void testGetUserTasks() throws Exception {
    	List<String> candidateGroups = new ArrayList<String>();
    	GroupQuery groupQuery = identityService.createGroupQuery().groupMember("李四");
    	List<Group> groupList = groupQuery.list();
    	if(null != groupList && groupList.size() > 0) {
    		for (Group group : groupList) {
    			candidateGroups.add(group.getId());
			}
    	}
    	
    	TaskQuery taskQuery = taskService.createTaskQuery();
    	taskQuery.taskCandidateGroupIn(candidateGroups);
    	List<Task> taskList = taskQuery.list();
    	if(null != taskList && taskList.size() > 0) {
    		for (Task task : taskList) {
				System.out.println(task.getId()+" " + task.getName());
			}
    	}
    	
    }
    
    @Test
    public void testClaim() throws Exception {
    	taskService.claim("92504", "王五");;
    }
    
    @Test
    public void testSetManager() throws Exception {
    	Group group = identityService.newGroup("部门经理");
    	group.setName("部门经理");
    	group.setType("deptManagerGroup");
    	identityService.saveGroup(group);
    	User user = identityService.newUser("李四");
    	identityService.saveUser(user);
    	identityService.createMembership("李四", "部门经理");;
    }
    
    @Test
    public void testSetManager2() throws Exception {
    	Group group = identityService.newGroup("总经理");
    	group.setName("总经理");
    	group.setType("managerGroup");
    	identityService.saveGroup(group);
    	User user = identityService.newUser("王五");
    	identityService.saveUser(user);
    	identityService.createMembership("王五", "总经理");;
    }
}
