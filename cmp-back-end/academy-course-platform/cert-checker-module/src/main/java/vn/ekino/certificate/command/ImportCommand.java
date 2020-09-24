package vn.ekino.certificate.command;

import info.magnolia.context.Context;
import info.magnolia.importexport.command.JcrImportCommand;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.dialog.DialogPresenter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.ekino.certificate.repository.AssessmentCriteriaRepository;
import vn.ekino.certificate.repository.CourseRepository;
import vn.ekino.certificate.repository.CourseResultRepository;
import vn.ekino.certificate.repository.OJTUserResultRepository;
import vn.ekino.certificate.repository.UserAttitudeResultRepository;
import vn.ekino.certificate.service.ExcelService;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.util.List;

public class ImportCommand extends JcrImportCommand {
    private final ExcelService excelService;

    private static final List<String> EXCEL_EXTENSION = List.of("xls", "xlsx");

    @Inject
    public ImportCommand(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        final String extension = FilenameUtils.getExtension(getFileName());
        if (EXCEL_EXTENSION.contains(extension)) {
            String repository = getRepository();
            Workbook workbook = "xlsx".equals(extension) ? new XSSFWorkbook(getStream()) : new HSSFWorkbook(getStream());
            SubAppContext uiContext = (SubAppContext) context.get("uiContext");
            DialogPresenter dialogPresenter = (DialogPresenter) context.get("dialogPresenter");

            switch (repository) {
                case CourseRepository.COURSE_WORKSPACE:
                    return executeExcelFile(workbook, uiContext, dialogPresenter);
                case OJTUserResultRepository.OJT_USER_RESULT_WORKSPACE:
                    return excelService.importOJTUserResult(workbook, uiContext, dialogPresenter);
                case CourseResultRepository.COURSE_RESULT_WORKSPACE:
                    excelService.importExcelToCourseResultWorkspace(workbook, uiContext, dialogPresenter);
                    return true;
                case UserAttitudeResultRepository.USER_ATTITUDE_RESULT_WORKSPACE:
                    return excelService.importAttitudeAssessmentsResult(workbook, uiContext, dialogPresenter);
                case AssessmentCriteriaRepository.WORKSPACE:
                    return excelService.importAssessment(workbook, uiContext);
                default:
                    throw new ActionExecutionException(String.format("Workspace [%s] dose not support import excel file.", getRepository()));
            }
        }
        return super.execute(context);
    }

    private boolean executeExcelFile(Workbook workbook, SubAppContext uiContext, DialogPresenter dialogPresenter) throws ActionExecutionException, RepositoryException {
        excelService.importExcelToWorkspace(workbook, uiContext, dialogPresenter);
        return true;
    }
}
