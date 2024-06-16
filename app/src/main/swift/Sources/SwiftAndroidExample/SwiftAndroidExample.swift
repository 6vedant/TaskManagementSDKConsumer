import Foundation
import Java
import TaskManagementSDK

// Note: Custom Swift/Java Object can't be passed from Swift to Java & Vice Versa as of now
// It will be available in future
// as of now, we will use String type to send from Swift to Java & Vice Versa

public func getTagsStringEquivalent(tags: [String]?) -> String {
        guard let tags = tags else {
            return ""
        }
        return tags.joined(separator: ",")
}

public func updateTasksList(activity: JObject) {
    let taskManager = TaskListViewModel.viewModel.getTaskManager()

    var allTasks = taskManager.getAllTasks()

    var taskIDArray = [String]()
    var taskTitleArray = [String]()
    var taskDescriptionArray = [String]()
    var dateCreatedArray = [Double]()
    var isCompletedArray = [Bool]()
    var tagsArray = [String]()
    var priorityArray = [Int32]()

    for task in allTasks {
        taskIDArray.append(task.id)
        taskTitleArray.append(task.title)
        taskDescriptionArray.append(task.descriptionTask ?? "")
        dateCreatedArray.append(task.dateCreated)
        isCompletedArray.append(task.isCompleted)

         // Safely unwrap the optional tags array and join with a separator, defaulting to an empty string if nil
            if let tags = task.tags {
                tagsArray.append(tags.joined(separator: ","))
            } else {
                tagsArray.append("")
            }
            priorityArray.append(Int32(task.priority ?? 0))
    }


    activity.call(method: "getAllUpdatedTasks", taskIDArray,
    taskTitleArray, taskDescriptionArray,
    isCompletedArray,dateCreatedArray, tagsArray, priorityArray)
}

public func updateSubTasksList(activity: JObject) {
    let taskManager = TaskListViewModel.viewModel.getTaskManager()

    var allSubTasks = taskManager.getAllSubTasks()

    var subTaskIDArray = [String]()
    var parentTaskIDArray = [String]()
    var subTaskTitleArray = [String]()
    var subTaskCompletedArray = [Bool]()


    for subTask in allSubTasks {
        subTaskIDArray.append(subTask.subTaskID)
        parentTaskIDArray.append(subTask.parentTaskID)
        subTaskTitleArray.append(subTask.subTaskTitle)
        subTaskCompletedArray.append(subTask.isSubTaskCompleted)
    }


    activity.call(method: "getAllUpdatedSubTasks", subTaskIDArray,
    parentTaskIDArray, subTaskTitleArray, subTaskCompletedArray)
}



// NOTE: Use @_silgen_name attribute to set native name for a function called from Java
@_silgen_name("Java_com_example_swiftandroidexample_ui_activity_MainActivity_initTaskManager")
public func MainActivity_initTaskManager(
    env: UnsafeMutablePointer<JNIEnv>, activity: JavaObject
) {
    // Create JObject wrapper for activity object
    let mainActivity = JObject(activity)
    updateTasksList(activity: mainActivity)
    let taskManager = TaskListViewModel.viewModel.getTaskManager()

     taskManager.subscribeToChanges { _ in
          updateTasksList(activity: mainActivity)
          updateSubTasksList(activity: mainActivity)
      }
}

// NOTE: Use @_silgen_name attribute to set native name for a function called from Java
@_silgen_name("Java_com_example_swiftandroidexample_ui_activity_MainActivity_addTask")
public func MainActivity_addTask(
    env: UnsafeMutablePointer<JNIEnv>, activity: JavaObject,
    taskID: JavaString, taskTitle: JavaString, taskDescription: JavaString,
    isCompleted: Bool, tags: JavaString, priority: Int32
) {
    // Convert the Java string to a Swift string
    let mainActivity = JObject(activity)
    let taskIDStr = String.fromJavaObject(taskID)
    let taskTitleStr = String.fromJavaObject(taskTitle)
    let taskDescriptionStr = String.fromJavaObject(taskDescription)
    let tagsStr = String.fromJavaObject(tags)

    let taskManager = TaskListViewModel.viewModel.getTaskManager()
    taskManager.addTask(id: taskIDStr, title: taskTitleStr, description: taskDescriptionStr, isCompleted: isCompleted, tags: tagsStr, priority: Int(priority))

}

// NOTE: Use @_silgen_name attribute to set native name for a function called from Java
@_silgen_name("Java_com_example_swiftandroidexample_ui_activity_MainActivity_addSubTask")
public func MainActivity_addSubTask(
    env: UnsafeMutablePointer<JNIEnv>, activity: JavaObject,
    subTaskID: JavaString, parentTaskID: JavaString, subTaskTitle: JavaString
) {
    // Convert the Java string to a Swift string
    let mainActivity = JObject(activity)
    let subTaskIDStr = String.fromJavaObject(subTaskID)
    let parentTaskIDStr = String.fromJavaObject(parentTaskID)
    let subTaskTitleStr = String.fromJavaObject(subTaskTitle)

    let taskManager = TaskListViewModel.viewModel.getTaskManager()
    taskManager.addSubtask(id: subTaskIDStr, parentTaskID: parentTaskIDStr, subTaskTitle: subTaskTitleStr)
    updateSubTasksList(activity: mainActivity)
}

// NOTE: Use @_silgen_name attribute to set native name for a function called from Java
@_silgen_name("Java_com_example_swiftandroidexample_ui_activity_MainActivity_deleteTask")
public func MainActivity_deleteTask(
    env: UnsafeMutablePointer<JNIEnv>, activity: JavaObject,
    taskID: JavaString
) {
    // Convert the Java string to a Swift string
    let mainActivity = JObject(activity)
   let taskIDStr = String.fromJavaObject(taskID)

    let taskManager = TaskListViewModel.viewModel.getTaskManager()

    for task in taskManager.getAllTasks() {
        if task.id == taskIDStr {
            taskManager.removeTask(task)
        }
    }
}

// NOTE: Use @_silgen_name attribute to set native name for a function called from Java
@_silgen_name("Java_com_example_swiftandroidexample_ui_activity_MainActivity_updateTask")
public func MainActivity_updateTask(
    env: UnsafeMutablePointer<JNIEnv>, activity: JavaObject,
    taskID: JavaString, newTaskTitle: JavaString, newDescription: JavaString,
    isCompleted: Bool, tags: JavaString, priority: Int32
) {
    // Convert the Java string to a Swift string
   let mainActivity = JObject(activity)
   let taskIDStr:String = String.fromJavaObject(taskID)
    let newTaskTitleStr = String.fromJavaObject(newTaskTitle)
 let newDescriptionStr = String.fromJavaObject(newDescription)
 let tagsStr = String.fromJavaObject(tags)

    let taskManager = TaskListViewModel.viewModel.getTaskManager()

    taskManager.updateTask(id: taskIDStr, newTitle: newTaskTitleStr,
    newDescription: newDescriptionStr, isCompleted: isCompleted, tags: tagsStr,
    priority: Int(priority))
}

// NOTE: Use @_silgen_name attribute to set native name for a function called from Java
@_silgen_name("Java_com_example_swiftandroidexample_ui_activity_MainActivity_deleteSubTask")
public func MainActivity_deleteSubTask(
    env: UnsafeMutablePointer<JNIEnv>, activity: JavaObject,
    subTaskID: JavaString
) {
    // Convert the Java string to a Swift string
    let mainActivity = JObject(activity)
    let subTaskIDStr = String.fromJavaObject(subTaskID)

    let taskManager = TaskListViewModel.viewModel.getTaskManager()

    taskManager.deleteSubTask(subTaskID: subTaskIDStr)
}


//ViewModel to manage tasks
class TaskListViewModel {
    static let viewModel = TaskListViewModel()
    weak var taskManagerViewModel: TaskManager?
    public init() {
        taskManagerViewModel = TaskManager.viewModel
    }

    public func getTaskManager() -> TaskManager {
            return taskManagerViewModel!
        }

}

