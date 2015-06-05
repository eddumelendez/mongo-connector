/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.api.automation;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.module.mongo.api.DBObjectsUnitTest;
import org.mule.module.mongo.api.FieldsSetUnitTest;
import org.mule.module.mongo.api.MongoCollectionUnitTest;
import org.mule.module.mongo.automation.RegressionTests;
import org.mule.module.mongo.automation.testcases.AddUserTestCases;
import org.mule.module.mongo.automation.testcases.CountObjectsTestCases;
import org.mule.module.mongo.automation.testcases.CountObjectsUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.CreateCollectionTestCases;
import org.mule.module.mongo.automation.testcases.CreateIndexTestCases;
import org.mule.module.mongo.automation.testcases.DropCollectionTestCases;
import org.mule.module.mongo.automation.testcases.DropDatabaseTestCases;
import org.mule.module.mongo.automation.testcases.DropIndexTestCases;
import org.mule.module.mongo.automation.testcases.DumpTestCases;
import org.mule.module.mongo.automation.testcases.ExecuteCommandTestCases;
import org.mule.module.mongo.automation.testcases.ExistsCollectionTestCases;
import org.mule.module.mongo.automation.testcases.FindFilesTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectFromMapTestCases;
import org.mule.module.mongo.automation.testcases.InsertObjectTestCases;
import org.mule.module.mongo.automation.testcases.ListCollectionTestCases;
import org.mule.module.mongo.automation.testcases.ListIndexesTestCases;
import org.mule.module.mongo.automation.testcases.MapReduceObjectsTestCases;
import org.mule.module.mongo.automation.testcases.legacy.CreateFileFromPayloadTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindFilesUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindObjectsTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindObjectsUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindOneFileTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindOneFileUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindOneObjectTestCases;
import org.mule.module.mongo.automation.testcases.legacy.FindOneObjectUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.GetFileContentTestCases;
import org.mule.module.mongo.automation.testcases.legacy.GetFileContentUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.IncrementalDumpTestCases;
import org.mule.module.mongo.automation.testcases.legacy.ListFilesTestCases;
import org.mule.module.mongo.automation.testcases.legacy.ListFilesUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.RemoveFilesTestCases;
import org.mule.module.mongo.automation.testcases.legacy.RemoveFilesUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.RemoveObjectsTestCases;
import org.mule.module.mongo.automation.testcases.legacy.RemoveObjectsUsingQueryMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.RestoreTestCases;
import org.mule.module.mongo.automation.testcases.legacy.SaveObjectFromMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.SaveObjectTestCases;
import org.mule.module.mongo.automation.testcases.legacy.UpdateObjectsByFunctionTestCases;
import org.mule.module.mongo.automation.testcases.legacy.UpdateObjectsByFunctionUsingMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.UpdateObjectsTestCases;
import org.mule.module.mongo.automation.testcases.legacy.UpdateObjectsUsingMapTestCases;
import org.mule.module.mongo.automation.testcases.legacy.UpdateObjectsUsingQueryMapTestCases;

@RunWith(Categories.class)
@IncludeCategory(RegressionTests.class)

@SuiteClasses({
	AddUserTestCases.class,
	CountObjectsTestCases.class,
	CountObjectsUsingQueryMapTestCases.class,
	CreateCollectionTestCases.class,
	CreateFileFromPayloadTestCases.class,
	CreateIndexTestCases.class,
	DBObjectsUnitTest.class,
	DropCollectionTestCases.class,
	DropDatabaseTestCases.class,
	DropIndexTestCases.class,
	DumpTestCases.class,
	ExecuteCommandTestCases.class,
	ExistsCollectionTestCases.class,
	FieldsSetUnitTest.class,
	FindFilesTestCases.class,
	FindFilesUsingQueryMapTestCases.class,
	FindObjectsTestCases.class,
	FindObjectsUsingQueryMapTestCases.class,
	FindOneFileTestCases.class,
	FindOneFileUsingQueryMapTestCases.class,
	FindOneObjectTestCases.class,
	FindOneObjectUsingQueryMapTestCases.class,
	GetFileContentTestCases.class,
	GetFileContentUsingQueryMapTestCases.class,
	IncrementalDumpTestCases.class,
	InsertObjectFromMapTestCases.class,
	InsertObjectTestCases.class,
	ListCollectionTestCases.class,
	ListFilesTestCases.class,
	ListFilesUsingQueryMapTestCases.class,
	ListIndexesTestCases.class,
	MapReduceObjectsTestCases.class,
	MongoCollectionUnitTest.class,
	RemoveFilesTestCases.class,
	RemoveFilesUsingQueryMapTestCases.class,
	RemoveObjectsTestCases.class,
	RemoveObjectsUsingQueryMapTestCases.class,
	RestoreTestCases.class,
	SaveObjectFromMapTestCases.class,
	SaveObjectTestCases.class,
	UpdateObjectsByFunctionTestCases.class,
	UpdateObjectsByFunctionUsingMapTestCases.class,
	UpdateObjectsTestCases.class,
	UpdateObjectsUsingMapTestCases.class,
	UpdateObjectsUsingQueryMapTestCases.class
	})

public class RegressionTestSuite {

}
