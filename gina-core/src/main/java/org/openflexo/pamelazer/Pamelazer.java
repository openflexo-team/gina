package org.openflexo.pamelazer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class Pamelazer {
	private CompilationUnit cu, pamCU;

	public Pamelazer() {
		this.cu = null;
		this.pamCU = null;
	}

	public boolean pamelazeFile(String path) throws FileNotFoundException {
		// creates an input stream for the file to be parsed
		FileInputStream in = new FileInputStream(path);

		try {
			// parse the file
			cu = JavaParser.parse(in);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.adapt();

		// prints the resulting compilation unit to default system output
		System.out.println(cu.toString());

		return true;
	}

	public void adapt() {
		createInterface();

		List<TypeDeclaration> types = cu.getTypes();
		for (TypeDeclaration type : types) {
			adaptType(type);
		}
	}

	public void createInterface() {
		pamCU = new CompilationUnit();
		pamCU.setPackage(this.cu.getPackage());
		pamCU.setImports(this.cu.getImports());
		pamCU.setComment(this.cu.getComment());
	}

	public void adaptType(TypeDeclaration type) {
		// listing

		List<Field> fields = new LinkedList<Field>();
		List<MethodDeclaration> methods = new LinkedList<MethodDeclaration>(), methodImpl = new LinkedList<MethodDeclaration>(),
				methodGetterSetter = new LinkedList<MethodDeclaration>();

		List<BodyDeclaration> members = type.getMembers();
		for (BodyDeclaration member : members) {
			// System.out.println(member.getClass().getName());
			if (member instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) member;

				List<VariableDeclarator> currentFields = field.getVariables();
				for (VariableDeclarator f : currentFields) {
					fields.add(new Field(f.getId(), field.getType(), field
							.getModifiers()));
				}

				// System.out.println(field.getType());
			}
		}

		for (BodyDeclaration member : members) {
			// System.out.println(member.getClass().getName());
			if (member instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) member;
				methods.add(method);
				if (isMethodGetterSetter(method.getName(), fields))
					methodGetterSetter.add(method);
				else
					methodImpl.add(method);
			}
		}

		// interface

		ClassOrInterfaceDeclaration in = new ClassOrInterfaceDeclaration(
				ModifierSet.PUBLIC, true, type.getName());
		ASTHelper.addTypeDeclaration(pamCU, in);

		in.getAnnotations().add(
				new NormalAnnotationExpr(new NameExpr("ModelEntity"), null));
		List<MemberValuePair> pairs = new LinkedList<MemberValuePair>();
		pairs.add(new MemberValuePair("xmlTag", type.getNameExpr()));
		in.getAnnotations().add(
				new NormalAnnotationExpr(new NameExpr("XMLElement"), pairs));

		// fields
		in.setMembers(new LinkedList<BodyDeclaration>());
		for (Field f : fields) {
			VariableDeclarator vd = new VariableDeclarator(f.getId(),
					new StringLiteralExpr(getPamelaFieldName(f.getId()
							.getName())));
			FieldDeclaration fd = new FieldDeclaration(ModifierSet.STATIC
					| ModifierSet.PUBLIC | ModifierSet.FINAL,
					new ClassOrInterfaceType("String"), vd);

			pairs.clear();
			pairs.add(new MemberValuePair("type", new NameExpr(f.getType()
					.toStringWithoutComments())));
			fd.getAnnotations().add(
					new NormalAnnotationExpr(
							new NameExpr("PropertyIdentifier"), pairs));

			in.getMembers().add(fd);
		}

		// methods
		// getters / setters
		for (MethodDeclaration m : methodImpl) {
			MethodDeclaration md = new MethodDeclaration(m.getModifiers(),
					m.getType(), m.getName(), m.getParameters());
			
			/*pairs.clear();
			pairs.add(new MemberValuePair("type", new NameExpr(f.getType()
					.toStringWithoutComments())));
			md.getAnnotations().add(
					new NormalAnnotationExpr(
							new NameExpr("PropertyIdentifier"), pairs));*/

			in.getMembers().add(md);
		}
		// other methods
		for (MethodDeclaration m : methodImpl) {
			/*
			 * VariableDeclarator vd = new VariableDeclarator(f.getId(), new
			 * StringLiteralExpr(getPamelaFieldName(f.getId().getName())));
			 * FieldDeclaration fd = new FieldDeclaration(ModifierSet.STATIC |
			 * ModifierSet.PUBLIC | ModifierSet.FINAL, new
			 * ClassOrInterfaceType("String"), vd);
			 * 
			 * pairs.clear(); pairs.add(new MemberValuePair("type", new
			 * NameExpr(f.getType().toStringWithoutComments())));
			 * fd.getAnnotations().add(new NormalAnnotationExpr(new
			 * NameExpr("PropertyIdentifier"), pairs));
			 */

			MethodDeclaration md = new MethodDeclaration(m.getModifiers(),
					m.getType(), m.getName(), m.getParameters());

			in.getMembers().add(md);
		}

		// implementation

		ClassOrInterfaceDeclaration impl = new ClassOrInterfaceDeclaration(
				ModifierSet.PUBLIC, false, type.getName() + "Impl");
		ASTHelper.addTypeDeclaration(pamCU, impl);

		impl.setExtends(new LinkedList<ClassOrInterfaceType>());
		impl.getExtends().add(new ClassOrInterfaceType(type.getName()));
		in.getAnnotations().add(
				new SingleMemberAnnotationExpr(new NameExpr(
						"ImplementationClass"), new NameExpr(impl.getName()
						+ ".class")));

		// methods
		impl.setMembers(new LinkedList<BodyDeclaration>());
		for (MethodDeclaration m : methodImpl) {
			/*
			 * VariableDeclarator vd = new VariableDeclarator(f.getId(), new
			 * StringLiteralExpr(getPamelaFieldName(f.getId().getName())));
			 * FieldDeclaration fd = new FieldDeclaration(ModifierSet.STATIC |
			 * ModifierSet.PUBLIC | ModifierSet.FINAL, new
			 * ClassOrInterfaceType("String"), vd);
			 * 
			 * pairs.clear(); pairs.add(new MemberValuePair("type", new
			 * NameExpr(f.getType().toStringWithoutComments())));
			 * fd.getAnnotations().add(new NormalAnnotationExpr(new
			 * NameExpr("PropertyIdentifier"), pairs));
			 */

			impl.getMembers().add(m);
		}
	}
	
	private boolean isMethodSetter(String name, List<Field> fields) {
		name = name.toLowerCase();

		for (Field f : fields) {
			if (name.endsWith(f.getId().getName())) {
				if (name.startsWith("set"))
					return true;
			}
		}

		return false;
	}

	private boolean isMethodGetterSetter(String name, List<Field> fields) {
		name = name.toLowerCase();

		for (Field f : fields) {
			if (name.endsWith(f.getId().getName())) {
				if (name.startsWith("is") || name.startsWith("get")
						|| name.startsWith("set"))
					return true;
			}
		}

		return false;
	}

	private String getPamelaFieldName(String name) {
		return name.toUpperCase();
	}

	public static void main(String[] args) throws Exception {
		Pamelazer p = new Pamelazer();

		p.pamelazeFile("D:/dt/GinaEvent.java");
	}
}
