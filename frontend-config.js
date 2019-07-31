import { DamlLfValue } from '@da/ui-core';

export const version = {
  schema: 'navigator-config',
  major: 2,
  minor: 0,
};

export const customViews = (userId, party, role) => ({
  mithra_operator: {
    type: "table-view",
    title: "Mithra Operator",
    source: {
      type: "contracts",
      filter: [
        {
          field: "argument.operator",
          value: party,
        },
        {
          field: "template.id",
          value: "FAT.Onboarding:Operator"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  mithra_user: {
    type: "table-view",
    title: "Mithra User",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "FAT.Onboarding:User"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  transfers: {
    type: "table-view",
    title: "FAT Token Transfers",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "FAT.Transfer:"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "template.id",
        title: "Type",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? "Signed" : DamlLfValue.toJSON(rowData.argument).txToSign ? "Unsigned" : "Request"
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.from",
        title: "From",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).from
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.to",
        title: "To",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).to
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.value",
        title: "Value",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).value)
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.sendStatus",
        title: "Status",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? JSON.stringify(DamlLfValue.toJSON(rowData.argument).sendStatus) : ""
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
})
