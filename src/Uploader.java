/*
 *	Hans K Formon
 *	Uploader.java
 *	NBCUniversal
 *	2014-01-07
 *
 *	This desktop application is an initial 'Hello World' implementation which makes 
 *	use of the video services APIs being developed for DPIM.  It provides a GUI to
 *	upload a file to our S3 bucket.
 */

import java.util.List;
import java.io.File;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.nbcuni.dpim.vidservices.DpimUploadService;
import com.amazonaws.services.s3.model.Bucket;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.FileTransfer;

import com.amazonaws.services.s3.model.PutObjectResult;
import org.eclipse.swt.widgets.Combo;

public class Uploader {

	protected Shell shell;
	private Text filePathText;
	
	private DpimUploadService uploadService;
	private List<Bucket> buckets;
	private String targetBucket;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Uploader window = new Uploader();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("S3 Uploader");
		
		final Label statusLabel = new Label(shell, SWT.NONE);
		statusLabel.setAlignment(SWT.CENTER);
		statusLabel.setBounds(10, 203, 290, 40);
		statusLabel.setText("");
		
		Button uploadButton = new Button(shell, SWT.NONE);
		uploadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = filePathText.getText();
				File f = new File(path);
				String name = f.getName();
				
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy h:mm:ss a");
				String formattedDate = sdf.format(d);
				
				statusLabel.setText("Attempting to upload file: " + name + " to S3 bucket: " + targetBucket);
				PutObjectResult res = uploadService.putObject(targetBucket, name, f);
				
				if (uploadService.getObject(targetBucket, name) != null) statusLabel.setText("Sucessfully uploaded " + name);
			}
		});
		uploadButton.setToolTipText("Upload the selected file to our S3 bucket in the AWS cloud.");
		uploadButton.setBounds(336, 219, 88, 33);
		uploadButton.setText("Upload to S3");
		
		filePathText = new Text(shell, SWT.BORDER);
		filePathText.setText("No file selected...");
		filePathText.setBounds(81, 56, 209, 21);
		
		Button fileSelectButton = new Button(shell, SWT.NONE);
		fileSelectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell , SWT.OPEN);
				fd.setText("Open");
				filePathText.setText(fd.open());
			}
		});
		fileSelectButton.setBounds(294, 54, 75, 25);
		fileSelectButton.setText("Select File");
		
		Label instructionsLabel1 = new Label(shell, SWT.WRAP | SWT.CENTER);
		instructionsLabel1.setBounds(92, 10, 266, 40);
		instructionsLabel1.setText("Select a file by using the dialog below or by dragging and dropping the file into this window.");
		
		DropTarget dropTarget = new DropTarget(shell, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		Transfer[] types = new Transfer[] {FileTransfer.getInstance()};
		dropTarget.setTransfer(types);
		dropTarget.addDropListener(new DropTargetListener() {
            public void dragEnter(DropTargetEvent event) {};
            public void dragOver(DropTargetEvent event) {};
            public void dragLeave(DropTargetEvent event) {};
            public void dragOperationChanged(DropTargetEvent event) {};
            public void dropAccept(DropTargetEvent event) {}
			public void drop(DropTargetEvent event) {
				filePathText.setText(((String[]) event.data)[0]);
			}
		});
		
		uploadService = new DpimUploadService();
		buckets = uploadService.getBuckets();

		final Combo s3Buckets = new Combo(shell, SWT.READ_ONLY);
		s3Buckets.setItems(new String[] {});
		s3Buckets.setBounds(227, 133, 90, 23);
		s3Buckets.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetBucket = s3Buckets.getText();
			}
		});
		
		for (Bucket b : buckets)
			s3Buckets.add(b.getName());
		
		s3Buckets.select(0);
		targetBucket = s3Buckets.getText();
		
		Label instructionsLabel2 = new Label(shell, SWT.NONE);
		instructionsLabel2.setAlignment(SWT.CENTER);
		instructionsLabel2.setBounds(80, 100, 290, 15);
		instructionsLabel2.setText("Select a target S3 bucket for this file upload.");
		
		Label s3ComboLabel = new Label(shell, SWT.NONE);
		s3ComboLabel.setBounds(133, 136, 90, 15);
		s3ComboLabel.setText("Target S3 Bucket");
	}
}
